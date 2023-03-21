package org.example.sftp.synchronizer;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.ResettableFileListFilter;
import org.springframework.integration.file.filters.ReversibleFileListFilter;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.synchronizer.InboundFileSynchronizer;
import org.springframework.integration.file.support.FileUtils;
import org.springframework.integration.metadata.MetadataStore;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessagingException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Base class charged with knowing how to connect to a remote file system,
 * scan it for new files and then download the files.
 * <p>
 * The implementation should run through any configured
 * {@link FileListFilter}s to
 * ensure the file entry is acceptable.
 *
 * @param <F> the Type that represents a remote file.
 */
public abstract class AbstractInboundFileSynchronizer<F>
        implements InboundFileSynchronizer, BeanFactoryAware, BeanNameAware, InitializingBean, Closeable {

    protected static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    protected final Log logger = LogFactory.getLog(this.getClass()); // NOSONAR

    private final RemoteFileTemplate<F> remoteFileTemplate;

    private EvaluationContext evaluationContext;

    private String remoteFileSeparator = "/";

    /**
     * Extension used when downloading files. We change it right after we know it's downloaded.
     */
    private String temporaryFileSuffix = ".writing";

    private Expression localFilenameGeneratorExpression;

    /**
     * the path on the remote mount as a String.
     */
    private Expression remoteDirectoryExpression;

    /**
     * An {@link FileListFilter} that runs against the <em>remote</em> file system view.
     */
    @Nullable
    private FileListFilter<F> filter;

    /**
     * Should we <em>delete</em> the remote <b>source</b> files
     * after copying to the local directory? By default this is false.
     */
    private boolean deleteRemoteFiles;

    /**
     * Should we <em>transfer</em> the remote file <b>timestamp</b>
     * to the local file? By default this is false.
     */
    private boolean preserveTimestamp;

    private BeanFactory beanFactory;

    @Nullable
    private Comparator<F> comparator;

    private MetadataStore remoteFileMetadataStore = new SimpleMetadataStore();

    private String metadataStorePrefix;

    private String name;

    /**
     * Create a synchronizer with the {@link SessionFactory} used to acquire {@link Session} instances.
     *
     * @param sessionFactory The session factory.
     */
    public AbstractInboundFileSynchronizer(SessionFactory<F> sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
        this.remoteFileTemplate = new RemoteFileTemplate<F>(sessionFactory);
    }

    @Nullable
    protected Comparator<F> getComparator() {
        return this.comparator;
    }

    /**
     * Set a comparator to sort the retrieved list of {@code F} (the Type that represents
     * the remote file) prior to applying filters and max fetch size.
     *
     * @param comparator the comparator.
     * @since 5.1
     */
    public void setComparator(@Nullable Comparator<F> comparator) {
        this.comparator = comparator;
    }

    /**
     * @param remoteFileSeparator the remote file separator.
     * @see RemoteFileTemplate#setRemoteFileSeparator(String)
     */
    public void setRemoteFileSeparator(String remoteFileSeparator) {
        Assert.notNull(remoteFileSeparator, "'remoteFileSeparator' must not be null");
        this.remoteFileSeparator = remoteFileSeparator;
    }

    /**
     * Set an expression used to determine the local file name.
     *
     * @param localFilenameGeneratorExpression the expression.
     */
    public void setLocalFilenameGeneratorExpression(Expression localFilenameGeneratorExpression) {
        Assert.notNull(localFilenameGeneratorExpression, "'localFilenameGeneratorExpression' must not be null");
        this.localFilenameGeneratorExpression = localFilenameGeneratorExpression;
    }

    /**
     * Set an expression used to determine the local file name.
     *
     * @param localFilenameGeneratorExpression the expression.
     * @see #setRemoteDirectoryExpression(Expression)
     * @since 4.3.13
     */
    public void setLocalFilenameGeneratorExpressionString(String localFilenameGeneratorExpression) {
        setLocalFilenameGeneratorExpression(EXPRESSION_PARSER.parseExpression(localFilenameGeneratorExpression));
    }

    /**
     * Set a temporary file suffix to be used while transferring files. Default ".writing".
     *
     * @param temporaryFileSuffix the file suffix.
     */
    public void setTemporaryFileSuffix(String temporaryFileSuffix) {
        this.temporaryFileSuffix = temporaryFileSuffix;
    }

    /**
     * Specify the full path to the remote directory.
     *
     * @param remoteDirectory The remote directory.
     */
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectoryExpression = new LiteralExpression(remoteDirectory);
    }

    /**
     * Specify an expression that evaluates to the full path to the remote directory.
     *
     * @param remoteDirectoryExpression The remote directory expression.
     * @since 4.2
     */
    public void setRemoteDirectoryExpression(Expression remoteDirectoryExpression) {
        doSetRemoteDirectoryExpression(remoteDirectoryExpression);
    }

    /**
     * Specify an expression that evaluates to the full path to the remote directory.
     *
     * @param remoteDirectoryExpression The remote directory expression.
     * @see #setRemoteDirectoryExpression(Expression)
     * @since 4.3.13
     */
    public void setRemoteDirectoryExpressionString(String remoteDirectoryExpression) {
        doSetRemoteDirectoryExpression(EXPRESSION_PARSER.parseExpression(remoteDirectoryExpression));
    }


    protected final void doSetRemoteDirectoryExpression(Expression expression) {
        Assert.notNull(expression, "'remoteDirectoryExpression' must not be null");
        this.remoteDirectoryExpression = expression;
    }

    /**
     * Set the filter to be applied to the remote files before transferring.
     *
     * @param filter the file list filter.
     */
    public void setFilter(@Nullable FileListFilter<F> filter) {
        doSetFilter(filter);
    }

    protected final void doSetFilter(@Nullable FileListFilter<F> filterToSet) {
        this.filter = filterToSet;
    }

    /**
     * Set to true to enable deletion of remote files after successful transfer.
     *
     * @param deleteRemoteFiles true to delete.
     */
    public void setDeleteRemoteFiles(boolean deleteRemoteFiles) {
        this.deleteRemoteFiles = deleteRemoteFiles;
    }

    /**
     * Set to true to enable the preservation of the remote file timestamp when
     * transferring.
     *
     * @param preserveTimestamp true to preserve.
     */
    public void setPreserveTimestamp(boolean preserveTimestamp) {
        this.preserveTimestamp = preserveTimestamp;
    }

    /**
     * Configure a {@link MetadataStore} to hold a remote file info (host, port, remote directory)
     * to transfer downstream in message headers when local file is pulled.
     *
     * @param remoteFileMetadataStore the {@link MetadataStore} to use.
     * @since 5.2
     */
    public void setRemoteFileMetadataStore(MetadataStore remoteFileMetadataStore) {
        this.remoteFileMetadataStore = remoteFileMetadataStore;
    }

    /**
     * Specify a prefix for keys in metadata store do not clash with other keys in the shared store.
     *
     * @param metadataStorePrefix the prefix to use.
     * @see #setRemoteFileMetadataStore(MetadataStore)
     * @since 5.2
     */
    public void setMetadataStorePrefix(String metadataStorePrefix) {
        this.metadataStorePrefix = metadataStorePrefix;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public final void afterPropertiesSet() {
        Assert.state(this.remoteDirectoryExpression != null, "'remoteDirectoryExpression' must not be null");
        if (this.evaluationContext == null) {
            this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(this.beanFactory);
        }
        if (!StringUtils.hasText(this.metadataStorePrefix)) {
            this.metadataStorePrefix = this.name;
        }
        doInit();
    }


    /**
     * Subclasses can override to perform initialization - called from
     * {@link InitializingBean#afterPropertiesSet()}.
     */
    protected void doInit() {
    }

    protected final List<F> filterFiles(F[] files) {
        return (this.filter != null) ? this.filter.filterFiles(files) : Arrays.asList(files);
    }

    protected String getTemporaryFileSuffix() {
        return this.temporaryFileSuffix;
    }

    @Override
    public void close() throws IOException {
        if (this.filter instanceof Closeable) {
            ((Closeable) this.filter).close();
        }
    }

    @Override
    public void synchronizeToLocalDirectory(final File localDirectory) {
        synchronizeToLocalDirectory(localDirectory, Integer.MIN_VALUE);
    }

    @Override
    public void synchronizeToLocalDirectory(final File localDirectory, final int maxFetchSize) {
        if (maxFetchSize == 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Max Fetch Size is zero - fetch to " + localDirectory.getAbsolutePath() + " ignored");
            }
            return;
        }
        String remoteDirectory = this.remoteDirectoryExpression.getValue(this.evaluationContext, String.class);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Synchronizing " + remoteDirectory + " to " + localDirectory);
        }
        try {
            int transferred = this.remoteFileTemplate.execute(session ->
                    transferFilesFromRemoteToLocal(remoteDirectory, localDirectory, maxFetchSize, session));
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(transferred + " files transferred from '" + remoteDirectory + "'");
            }
        } catch (Exception e) {
            throw new MessagingException("Problem occurred while synchronizing '"
                    + remoteDirectory + "' to local directory", e);
        }
    }

    private void walkDirectory(Session<F> session, String remoteDir, Map<String, F> collector) throws IOException {
        F[] files = session.list(remoteDir);
        for (F file : files) {
            if (isFile(file)) {
                collector.put(remoteDir + remoteFileSeparator + getFilename(file), file);
            } else if (!isHide(file)) {
                walkDirectory(session, remoteDir + remoteFileSeparator + getFilename(file), collector);
            }
        }
    }

    private Integer transferFilesFromRemoteToLocal(String remoteDirectory, File localDirectory,
                                                   int maxFetchSize, Session<F> session) throws IOException {
        if (session.exists(remoteDirectory)) {
            Map<String, F> entries = new HashMap<>();
            walkDirectory(session, remoteDirectory, entries);

            if (!ObjectUtils.isEmpty(entries)) {
                int copied = entries.size();
                int accepted = 0;

                EvaluationContext localFileEvaluationContext = null;
                if (this.localFilenameGeneratorExpression != null) {
                    localFileEvaluationContext = ExpressionUtils.createStandardEvaluationContext(this.beanFactory);
                    localFileEvaluationContext.setVariable("remoteDirectory", remoteDirectory);
                }

                for (Map.Entry<String, F> entry : entries.entrySet()) {
                    if ((maxFetchSize < 0 || accepted < maxFetchSize) && this.filter.accept(entry.getValue())) {
                        accepted++;
                    } else {
                        entry = null;
                        copied--;
                    }
                    copied = copyIfNotNull(remoteDirectory, localDirectory, localFileEvaluationContext, session, copied, entry);
                }

                return copied;
            }
        }

        return 0;
    }

    private int copyIfNotNull(String remoteDirectory, File localDirectory,
                              @Nullable EvaluationContext localFileEvaluationContext,
                              Session<F> session,
                              int copied, @Nullable Map.Entry<String, F> entry) throws IOException {

        boolean renamedFailed = false;
        try {
            if (entry != null &&
                    !copyFileToLocalDirectory(remoteDirectory, localFileEvaluationContext, entry, localDirectory,
                            session)) {

                renamedFailed = true;
            }
        } catch (RuntimeException | IOException e1) {
            resetFilterIfNecessary(entry.getValue());
            throw e1;
        }
        return renamedFailed ? copied - 1 : copied;
    }

    protected void rollbackFromFileToListEnd(List<F> filteredFiles, F file) {
        if (this.filter instanceof ReversibleFileListFilter) {
            ((ReversibleFileListFilter<F>) this.filter)
                    .rollback(file, filteredFiles);
        }
    }

    protected boolean copyFileToLocalDirectory(String remoteDirectoryPath, // '/opt/data1/test-data/product'
                                               @Nullable EvaluationContext localFileEvaluationContext,
											   Map.Entry<String, F> remoteEntry,
											   File localDirectory,
                                               Session<F> session) throws IOException {
		// 将预设模式匹配成功的文件 remoteFile 所在目录下所有文件传输到本地，最后将模式匹配文件传输到本地

		Path remoteFilePath = Paths.get(remoteEntry.getKey());
		Path remoteFileParentPath = remoteFilePath.getParent();


		F[] files = session.list(remoteFileParentPath.toString());
		if (!ObjectUtils.isEmpty(files)) {
			files = FileUtils.purgeUnwantedElements(files, e -> !isFile(e), this.comparator);
		}

		if (!ObjectUtils.isEmpty(files)) {
            for (F file : files) {
                String remoteFileName = getFilename(file);
                Path toTransFilePath = remoteFileParentPath.resolve(remoteFileName);
                Path relativePath  = Paths.get(remoteDirectoryPath).relativize(remoteFileParentPath); // 20220615/w03/pre=0
                String localFileName = generateLocalFileName(relativePath.resolve(remoteFileName).toString(), localFileEvaluationContext);

                long modified = getModified(remoteEntry.getValue());

                File localFile = new File(localDirectory, localFileName);
                boolean exists = localFile.exists();
                if (!exists || (this.preserveTimestamp && modified != localFile.lastModified())) {
                    if (!exists && localFileName.replaceAll("/", Matcher.quoteReplacement(File.separator)).contains(File.separator)) {
                        localFile.getParentFile().mkdirs(); //NOSONAR - will fail on the writing below
                    }

                    boolean transfer = true;

                    if (exists && !localFile.delete()) {
                        transfer = false;
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Cannot delete local file '" + localFile +
                                    "' in order to transfer modified remote file '" + remoteEntry.getValue() + "'. " +
                                    "The local file may be busy in some other process.");
                        }
                    }

                    boolean renamed = false;

                    if (transfer) {
                        renamed = copyRemoteContentToLocalFile(session, toTransFilePath.toString(), localFile);
                    }

                    if (renamed) {
                        if (this.deleteRemoteFiles) {
                            session.remove(toTransFilePath.toString());
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("deleted remote file: " + remoteFilePath);
                            }
                        }
                        if (this.preserveTimestamp && !localFile.setLastModified(modified)) {
                            throw new IllegalStateException("Could not sent last modified on file: " + localFile);
                        }
                        String hostPort = session.getHostPort();
                        int colonIndex = hostPort.lastIndexOf(':');
                        String host = hostPort.substring(0, colonIndex);
                        String port = hostPort.substring(colonIndex + 1);
                        try {
                            String remoteFileMetadata =
                                    new URI(protocol(), null, host, Integer.parseInt(port),
                                            remoteFileParentPath.toString(), null, remoteFileName)
                                            .toString();
                            this.remoteFileMetadataStore.put(buildMetadataKey(localFile), remoteFileMetadata);
                        } catch (URISyntaxException ex) {
                            throw new IllegalStateException("Cannot create a remote file metadata", ex);
                        }
                        return true;
                    } else {
                        resetFilterIfNecessary(remoteEntry.getValue());
                    }
                } else if (this.logger.isWarnEnabled()) {
                    this.logger.warn("The remote file '" + remoteEntry.getValue() + "' has not been transferred " +
                            "to the existing local file '" + localFile + "'. Consider removing the local file.");
                }
            }
		}

        return false;
    }

    private void resetFilterIfNecessary(F remoteFile) {
        if (this.filter instanceof ResettableFileListFilter) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Removing the remote file '" + remoteFile +
                        "' from the filter for a subsequent transfer attempt");
            }
            ((ResettableFileListFilter<F>) this.filter).remove(remoteFile);
        }
    }

    private boolean copyRemoteContentToLocalFile(Session<F> session, String remoteFilePath, File localFile) {
        boolean renamed;
        String tempFileName = localFile.getAbsolutePath() + this.temporaryFileSuffix;
        File tempFile = new File(tempFileName);

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            session.read(remoteFilePath, outputStream);
        } catch (RuntimeException e) { // NOSONAR catch and throw
            throw e;
        } catch (Exception e) {
            throw new MessagingException("Failure occurred while copying '" + remoteFilePath
                    + "' from the remote to the local directory", e);
        }

        renamed = tempFile.renameTo(localFile);

        if (!renamed) {
            if (localFile.delete()) {
                renamed = tempFile.renameTo(localFile);
                if (!renamed && this.logger.isInfoEnabled()) {
                    this.logger.info("Cannot rename '"
                            + tempFileName
                            + "' to local file '" + localFile + "' after deleting. " +
                            "The local file may be busy in some other process.");
                }
            } else if (this.logger.isInfoEnabled()) {
                this.logger.info("Cannot delete local file '" + localFile +
                        "'. The local file may be busy in some other process.");
            }
        }
        return renamed;
    }

    private String generateLocalFileName(String remoteFileName,
                                         @Nullable EvaluationContext localFileEvaluationContext) {

        if (this.localFilenameGeneratorExpression != null) {
            return this.localFilenameGeneratorExpression.getValue(localFileEvaluationContext, remoteFileName,
                    String.class);
        }
        return remoteFileName;
    }

    /**
     * Obtain a metadata for remote file associated with the provided local file.
     *
     * @param localFile the local file to retrieve metadata for.
     * @return the metadata for remove file in the URI style:
     * {@code protocol://host:port/remoteDirectory#remoteFileName}
     * @since 5.2
     */
    @Nullable
    public String getRemoteFileMetadata(File localFile) {
        String metadataKey = buildMetadataKey(localFile);
        return this.remoteFileMetadataStore.get(metadataKey);
    }

    /**
     * Remove a metadata for remote file associated with the provided local file.
     *
     * @param localFile the local file to remove metadata for.
     * @since 5.2
     */
    public void removeRemoteFileMetadata(File localFile) {
        String metadataKey = buildMetadataKey(localFile);
        this.remoteFileMetadataStore.remove(metadataKey);
    }

    private String buildMetadataKey(File file) {
        return this.metadataStorePrefix + file.getAbsolutePath();
    }

    protected abstract boolean isFile(F file);

    protected abstract boolean isHide(F file);

    protected abstract String getFilename(F file);

    protected abstract long getModified(F file);

    /**
     * Return the protocol this synchronizer works with.
     *
     * @return the protocol this synchronizer works with.
     * @since 5.2
     */
    protected abstract String protocol();

}
