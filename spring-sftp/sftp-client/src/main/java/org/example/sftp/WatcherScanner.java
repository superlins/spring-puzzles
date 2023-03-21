package org.example.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

public class WatcherScanner implements ApplicationEventPublisherAware, SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(WatcherScanner.class);

    private final AtomicBoolean running = new AtomicBoolean();

    private WatchEvent.Kind<?>[] kinds = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};

    private boolean autoCreateDirectory = true;

    private FileListFilter<File> filter;

    private Path watchable;

    private ApplicationEventPublisher eventPublisher;

    public void setFilter(FileListFilter<File> filter) {
        Assert.notNull(filter, "'filter' must not be null");
        this.filter = filter;
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
    }

    public void setWatchable(Path watchable) {
        Assert.notNull(watchable, "watchable must not be null");
        this.watchable = watchable;
    }

    public void setKinds(WatchEvent.Kind<?>[] kinds) {
        this.kinds = kinds;
    }

    @Override
    public void start() {
        if (!this.running.getAndSet(true)) {
            if (!Files.exists(watchable) && this.autoCreateDirectory && !watchable.toFile().mkdirs()) {
                throw new IllegalStateException("Cannot create directory or its parents: " + this.watchable);
            }
            Assert.isTrue(Files.exists(watchable),
                    () -> "Source directory [" + this.watchable + "] does not exist.");
            Assert.isTrue(Files.isDirectory(watchable),
                    () -> "Source directory [" + this.watchable + "] does not point to a directory.");
            Assert.isTrue(Files.isReadable(this.watchable),
                    () -> "Source directory [" + this.watchable + "] is not readable.");

            Watcher watcher = new Watcher(watchable, kinds);
            watcher.watch();
        }
    }

    @Override
    public void stop() {
        this.running.getAndSet(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    private class Watcher implements Runnable {

        private final ConcurrentMap<Path, WatchKey> watchKeys = new ConcurrentHashMap<>();

        private final ThreadFactory watcherThreadFactory = new WatcherThreadFactory();

        private final Path watchable;

        private final WatchEvent.Kind<?>[] events;

        public Watcher(Path watchable, WatchEvent.Kind<?>[] events) {
            if (notExists(watchable)) {
                throw new IllegalArgumentException("watchable not found " + watchable);
            }
            this.watchable = watchable;
            this.events = events;
        }

        public void watch() {
            if (exists(watchable))
                watcherThreadFactory.newThread(this).start();
        }

        @Override
        public void run() {
            try (WatchService watchService = getDefault().newWatchService()) {
                register(watchService, watchable);
                while (WatcherScanner.this.isRunning()) {
                    pollEvents(watchService);
                    if (watchKeys.isEmpty()) {
                        break;
                    }
                }
            } catch (IOException | ClosedWatchServiceException e) {
                logger.error("Watcher-Thread interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        private void register(WatchService watchService, Path watchable) {
            try {
                walkFileTree(watchable, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        FileVisitResult fileVisitResult = super.preVisitDirectory(dir, attrs);
                        doRegister(watchService, dir);
                        return fileVisitResult;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        FileVisitResult fileVisitResult = super.visitFile(file, attrs);
                        if (WatcherScanner.this.filter.accept(file.toFile())) {
                            WatcherScanner.this.eventPublisher.publishEvent(file);
                        }
                        return fileVisitResult;
                    }
                });
            } catch (IOException e) {
                logger.error("Failed to walk directory: " + watchable);
            }
        }

        private void pollEvents(WatchService watchService) {
            WatchKey key = watchService.poll();
            if (key != null) {
                key.pollEvents().forEach(event -> {
                    logger.info("watched event: {}" + event);
                    Path watched = ((Path) key.watchable()).resolve(cast(event).context());
                    if (WatcherScanner.this.filter.accept(watched.toFile())) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == ENTRY_CREATE) {
                            if (isDirectory(watched, NOFOLLOW_LINKS)) {
                                register(watchService, watched);
                            }
                            WatcherScanner.this.eventPublisher.publishEvent(watched);
                        } else if (kind == ENTRY_MODIFY) {
                            WatcherScanner.this.eventPublisher.publishEvent(watched);
                        } else if (kind == ENTRY_DELETE) {
                            WatcherScanner.this.eventPublisher.publishEvent(watched);
                        }
                    }
                });
                if (!key.reset()) {
                    logger.info("deregister watchable: {}", key.watchable());
                    watchKeys.remove(key);
                }
            }
        }

        private void doRegister(WatchService watchService, Path dir) throws IOException {
            if (!watchKeys.containsKey(dir)) {
                logger.debug("register watchable: {}", dir);
                WatchKey register = dir.register(watchService, events);
                watchKeys.put(dir, register);
            }
        }

        private WatchEvent<Path> cast(WatchEvent<?> event) {
            return (WatchEvent<Path>) event;
        }
    }

    private static class WatcherThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        private final String namePrefix;

        WatcherThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "Watcher-T-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (!t.isDaemon())
                t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}