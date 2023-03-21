package org.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.example.sftp.synchronizer.SftpInboundFileSynchronizer;
import org.example.sftp.synchronizer.SftpInboundFileSynchronizingMessageSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.annotation.InboundChannelAdapterAnnotationPostProcessor;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.remote.FileInfo;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// AdvisedRequestHandler && MetricsCaptor
@SpringBootApplication
public class SftpClientApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SftpClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        SftpGateway gateway = context.getBean(SftpGateway.class);
        List<FileInfo<ChannelSftp.LsEntry>> fileInfos = gateway.lsR("oftp/00/536");
        fileInfos.sort(Comparator.comparing(FileInfo::getModified));
        // gateway.send("/Users/renc/iCoder/sftp/server/20220621", new File("/Users/renc/Nutstore Files/我的坚果云/Resources/ws/IdeaProjects/spring-sftp/sftp-client/client-dir/_SUCCESS"));
        fileInfos.forEach(f -> System.out.println(f.getFilename()));
    }

    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate() {
        return new SftpRemoteFileTemplate(sftpSessionFactory());
    }

    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("10.50.30.243");
        factory.setPort(32222);
        factory.setUser("oftp");
        factory.setPassword("oftp@2022");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }

    // Gateway

    @Bean("ls-l-Gateway")
    @ServiceActivator(inputChannel = "ls-l-Channel")
    public MessageHandler lsRGateway(SessionFactory<ChannelSftp.LsEntry> sessionFactory) {
        SftpOutboundGateway ls = new SftpOutboundGateway(sessionFactory, "ls", "payload");
        ls.setOption(AbstractRemoteFileOutboundGateway.Option.NAME_ONLY);
        return ls;
    }

    @Bean("ls-R-Gateway")
    @ServiceActivator(inputChannel = "ls-R-Channel")
    public MessageHandler lsGateway(SessionFactory<ChannelSftp.LsEntry> sessionFactory) {
        SftpOutboundGateway ls = new SftpOutboundGateway(sessionFactory, "ls", "payload");
        ls.setOption(AbstractRemoteFileOutboundGateway.Option.RECURSIVE);
        return ls;
    }

    // IN-BOUNDER

    // @Bean
    // public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
    //     SftpInboundFileSynchronizer sftpInboundFileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
    //     sftpInboundFileSynchronizer.setDeleteRemoteFiles(false);
    //     sftpInboundFileSynchronizer.setRemoteDirectory("/Users/renc/iCoder/sftp/server");
    //     sftpInboundFileSynchronizer.setPreserveTimestamp(true);
    //     sftpInboundFileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("_SUCCESS"));
    //     sftpInboundFileSynchronizer.setTemporaryFileSuffix(".writing");
    //     return sftpInboundFileSynchronizer;
    // }
    //
    // /** @see InboundChannelAdapterAnnotationPostProcessor */
    // @Bean
    // @InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay = "5000"))
    // public MessageSource<File> sftpMessageSource() {
    //     SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
    //     source.setAutoCreateLocalDirectory(true);
    //     source.setLocalDirectory(new File("/Users/renc/iCoder/sftp/client"));
    //
    //     List<FileListFilter<File>> filters = new ArrayList<>();
    //     filters.add(new FileSystemPersistentAcceptOnceFileListFilter(new RedisMetadataStore(), "sftp-vendor-"));
    //     source.setLocalFilter(new CompositeFileListFilter<>(filters)); // 决定什么样的文件可以订阅方被接收
    //
    //     source.setUseWatchService(true);
    //     source.setMaxFetchSize(1);
    //     return source;
    // }
    //
    // @Bean
    // @ServiceActivator(inputChannel = "sftpChannel")
    // public MessageHandler fromSftpHandler() { // received files from a remote server
    //     return new MessageHandler() {
    //         @Override
    //         public void handleMessage(Message<?> message) throws MessagingException {
    //             System.out.println(message.getPayload());
    //         }
    //     };
    // }


    // OUT-BOUNDER

    // @Bean
    // @ConditionalOnMissingBean
    // public List<AbstractRequestHandlerAdvice> adviceChain(ObjectProvider<AbstractRequestHandlerAdvice> adviceProvider) {
    //     return adviceProvider.orderedStream().collect(Collectors.toList());
    // }
    //
    // @Bean
    // @ServiceActivator(inputChannel = "toSftpChannel", adviceChain = {"adviceChain"})
    // public MessageHandler toSftpHandler() { // transfer files to a remote server
    //     SftpMessageHandler handler = new SftpMessageHandler(sftpRemoteFileTemplate());
    //     handler.setAutoCreateDirectory(true);
    //     handler.setRemoteDirectoryExpressionString("headers['" + FileHeaders.REMOTE_DIRECTORY + "']");
    //     return handler;
    // }
    //
    // @MessagingGateway /** @see org.springframework.integration.gateway.GatewayProxyFactoryBean */
    // public interface MyGateway {
    //
    //     @Gateway(requestChannel = "toSftpChannel")
    //     void sendToSftp(@Header(FileHeaders.REMOTE_DIRECTORY) String remoteDir, File file);
    // }
    //
    // @Bean
    // public WatcherScanner watcherScanner() {
    //     WatcherScanner watcherScanner = new WatcherScanner();
    //     watcherScanner.setWatchable(Paths.get("/Users/renc/Downloads/test"));
    //     watcherScanner.setFilter(new AcceptOnceFileListFilter<>());
    //     return watcherScanner;
    // }
    //
    // @EventListener
    // public void listener(PayloadApplicationEvent payloadApplicationEvent) {
    //     System.out.println(">>>>>> PayloadApplicationEvent: " + payloadApplicationEvent.getPayload());
    // }

}