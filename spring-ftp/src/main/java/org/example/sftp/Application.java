package org.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Configuration
    static class Tester {

        @Bean
        public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
            DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
            factory.setHost("0.0.0.0");
            factory.setPort(22);
            factory.setUser("foo");
            factory.setPassword("pass");
            factory.setAllowUnknownKeys(true);
            return factory;
        }

        @Bean
        public SftpRemoteFileTemplate sftpRemoteFileTemplate() {
            return new SftpRemoteFileTemplate(sftpSessionFactory());
        }

        // inbound-channel-adapter
        @Bean
        public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
            SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
            fileSynchronizer.setDeleteRemoteFiles(false);
            fileSynchronizer.setRemoteDirectory("/upload");
            fileSynchronizer.setPreserveTimestamp(true);
            // fileSynchronizer.setLocalFilenameGeneratorExpressionString("rename local file name");
            fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.*"));
            return fileSynchronizer;
        }

        // outbound-channel-adapter
        @Bean
        @ServiceActivator(inputChannel = "sftpChannel")
        public MessageHandler handler() {
            // return new SftpMessageHandler(sftpRemoteFileTemplate());
            return new MessageHandler() {
                @Override
                public void handleMessage(Message<?> message) throws MessagingException {
                    System.out.println(message.getPayload());
                }
            };
        }

        @Bean
        @InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay = "60000"))
        public MessageSource<File> sftpMessageSource() {
            SftpInboundFileSynchronizingMessageSource source =
                    new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
            source.setLocalDirectory(new File("sftp-inbound"));
            source.setAutoCreateLocalDirectory(true);
            // source.setLocalFilter(new AcceptOnceFileListFilter<File>());
            // source.setMaxFetchSize(1);
            // source.setScanner(new RecursiveDirectoryScanner());
            return source;
        }
    }
}
