package main.java.ru.gb.network_storage.server.handler;

import db.DBConnection;
import entity.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private final int SIZE_BUF = 64 * 1024;
    private Connection connection;
    private Statement statement;
    private CommandMessage message;
    private RandomAccessFile accessFile;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws SQLException {
        connection = DBConnection.getConnection();
        statement = connection.createStatement();
        System.out.println("New active channel");
        CommandMessage message = new CommandMessage();
        message.setCommand(Command.CHANNEL_HAS_BEEN_ACTIVATED);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException, SQLException, InterruptedException {
        if (msg instanceof CommandMessage) {
            message = (CommandMessage) msg;
            System.out.println(message.getCommand());
            switch (message.getCommand()) {
                case DOWNLOADING_FROM_CLIENT: {
                    System.out.println(message.getFileForDownloading());
                    System.out.println(message.getPathForDownloading());
                    FileContent content = message.getFileContent();
                    if (content == null) {
                        ctx.writeAndFlush(message);
                    } else {
                        accessFile = new RandomAccessFile(message.getPathForDownloading() + "\\" + message.getFileForDownloading().getName(), "rw");
                        accessFile.seek(content.getStartPosition());
                        accessFile.write(content.getContent());
                        if (content.isLast()) {
                            System.out.println("File was copy.");
                            accessFile.close();
                            accessFile = null;
                        }
                    }
                }
                break;
                case DOWNLOADING_FROM_SERVER: {
                    if (message.getFileForDownloading().isDirectory()) {
                        copyInformation(message.getFileForDownloading(), new File(message.getPathForDownloading().toFile().getAbsolutePath() + "/" + message.getFileForDownloading().getName()), ctx);
                    } else {
                        copyFile(message.getFileForDownloading(), message.getPathForDownloading(), ctx);
                    }
                }
                break;
                case CLIENT_DISCONNECT: {
                    ctx.channel().close();
                }
                break;
                case REGISTRATION_NEW_CLIENT: {
                    statement.executeUpdate("INSERT INTO users (login, password)"
                            + "VALUES ('" + message.getUser().getUsername() + "', '" + message.getUser().getPassword() + "');");
                    message.setCommand(Command.REGISTRATION_CONFIRMED);
                    ctx.writeAndFlush(message);
                }
                break;
                case CREATE_DIRECTORY: {
                    System.out.println(message.getPathForDownloading());
                    if (!message.getPathForDownloading().toFile().exists()) {
                        message.getPathForDownloading().toFile().mkdir();
                    }
                }
                break;
            }
        }
    }

    private void copyInformation(File source, File destination, ChannelHandlerContext ctx) throws IOException, InterruptedException {
        if (source.isDirectory()) {
            message.setPathForDownloading(destination.toPath());
            message.setCommand(Command.CREATE_DIRECTORY);
            ctx.writeAndFlush(message);
            List<File> list = Arrays.stream(Objects.requireNonNull(source.listFiles())).sorted((o1, o2) -> {
                if (o1.isDirectory() && o2.isFile()) {
                    return 1;
                } else if (o1.isFile() && o2.isDirectory()) {
                    return -1;
                } else {
                    return o1.getName().compareTo(o2.getName());

                }
            }).collect(Collectors.toList());
            List<String> sourceList = list.stream().map(File::getName).collect(Collectors.toList());
            for (String child : sourceList) {
                File newSource = new File(source, child);
                File newDestination = new File(destination, child);
                copyInformation(newSource, newDestination, ctx);
            }
        } else {
            copyFile(source, destination.getParentFile().toPath(), ctx);
        }
    }
    private void copyFile(File source, Path path, ChannelHandlerContext ctx) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(source, "r");
        sendFile(accessFile, source, path, ctx);
    }

    private void sendFile(RandomAccessFile accessFile, File source, Path path, ChannelHandlerContext ctx) throws IOException {
        byte[] fileContent;
        long available = accessFile.length() - accessFile.getFilePointer();
        if (available > SIZE_BUF) {
            fileContent = new byte[SIZE_BUF];
        } else {
            fileContent = new byte[(int) available];
        }
        FileContent content = new FileContent();
        content.setStartPosition(accessFile.getFilePointer());
        accessFile.read(fileContent);
        content.setFileLength(accessFile.length());
        content.setContent(fileContent);
        boolean last = accessFile.getFilePointer() == accessFile.length();
        content.setLast(last);
        message.setFileContent(content);
        message.setFileForDownloading(source);
        message.setPathForDownloading(path);
        message.setCommand(Command.DOWNLOADING_FROM_SERVER);
        RandomAccessFile finalAccessFile = accessFile;
        ctx.channel().writeAndFlush(message).addListener(future -> {
            if (!last) {
                sendFile(finalAccessFile, source, path, ctx);
            }
        });
            if (last) {
                accessFile.close();
            }
        }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("client disconnect");
        if (accessFile != null) {
            accessFile.close();
        }
    }
}
