package org.example.controller;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.file.remote.session.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.Files.getFileStore;
import static java.nio.file.Files.walkFileTree;

/**
 * @author renc
 */
public class Tester {

    @Test
    public void testSPEL() {
        LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression("'/abc/df/'+T(java.time.LocalDate).of(2021, 01, 01).until(T(java.time.LocalDate).now(), T(java.time.temporal.ChronoUnit).DAYS)");
        System.out.println(expression.getValue());
    }

    @Test
    public void testDate() {
        LocalDate l1 = LocalDate.parse("20220509", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate l2 = LocalDate.parse("20210101", DateTimeFormatter.ofPattern("yyyyMMdd"));
        long days = l2.until(l1, ChronoUnit.DAYS);
        System.out.println(days);
    }

    @Test
    public void testWF() throws IOException {
        Files.walkFileTree(Paths.get("/Users/renc/Nutstore Files/我的坚果云/Resources/ws/IdeaProjects/spring-sftp/sftp-client/src/main/java"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                FileVisitResult fileVisitResult = super.preVisitDirectory(dir, attrs);
                System.out.println("preVisitDirectory >>>> " + dir);
                return fileVisitResult;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visitFile >>>> " + file);
                return super.visitFile(file, attrs);
            }
        });
    }

    @Test
    public void test() {
        String path = "/opt/data1/test-data/product/20220615/w03/pre=0/_SUCCESS";
        Path relativize2 = Paths.get("/opt/data1/test-data/product").relativize(Paths.get(path));
        System.out.println(relativize2);
    }
}
