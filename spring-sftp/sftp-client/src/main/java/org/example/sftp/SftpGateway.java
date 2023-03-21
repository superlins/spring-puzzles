package org.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.FileInfo;
import org.springframework.messaging.handler.annotation.Header;

import java.io.File;
import java.util.List;

/**
 * @see org.springframework.integration.gateway.GatewayProxyFactoryBean
 */
@MessagingGateway
public interface SftpGateway {

    @Gateway(requestChannel = "toSftpChannel")
    void send(@Header(FileHeaders.REMOTE_DIRECTORY) String remoteDir, File file);

    @Gateway(requestChannel = "ls-l-Channel")
    List<String> lsl(String dir);

    @Gateway(requestChannel = "ls-R-Channel")
    List<FileInfo<ChannelSftp.LsEntry>> lsR(String dir);
}