package org.example.sftp.server;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Paths;
import java.util.Collections;

@SpringBootApplication
public class SftpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SftpServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                SshServer sshd = SshServer.setUpDefaultServer();
                sshd.setPort(2222);
                sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("host.ser")));
                sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
                sshd.setPasswordAuthenticator((username, password, session) ->
                        username.equals("test") && password.equals("password"));
                sshd.start();
                System.out.println("》》》》》》》 SFTP server started《《《《《《《");
            }
        };
    }
}