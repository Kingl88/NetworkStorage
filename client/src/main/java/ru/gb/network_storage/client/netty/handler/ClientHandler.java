package ru.gb.network_storage.client.netty.handler;

import javafx.application.Platform;
import message.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.FileContent;
import message.Message;
import ru.gb.network_storage.client.controller.Client;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    private final int SIZE_BUF = 64 * 1024;
    private RandomAccessFile accessFile;
    private final Client client;

    private CommandMessage message;
    private final WindowsManager windowsManager = WindowsManager.getInstance();

    public ClientHandler(final Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        client.setCtx(ctx);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message msg) throws Exception {
        if (msg instanceof CommandMessage) {
            message = (CommandMessage) msg;
            switch (message.getCommand()) {
                case CHANNEL_HAS_BEEN_ACTIVATED: {
                    Platform.runLater(() -> {
                        client.getWindowsManager().getMainGUIController().labelServer.setText("Server online");
                    });
                }
                break;
                case AUTHORIZATION_CONFIRMED: {
                    Platform.runLater(() -> {
                        windowsManager.getAuthController().closeWindow();
                        windowsManager.getMainGUIController().labelClient.setText("Client connected");
                        windowsManager.getMainGUIController().serverListView.setVisible(true);
                        String pathClient = client.getDEFAULT_FOLDER_ON_CLIENT_SIDE().getAbsolutePath();
                        File dirClient = new File(pathClient);
                        client.setCurrentFolderOnClientSide(new File(client.getDEFAULT_FOLDER_ON_CLIENT_SIDE().getAbsolutePath()));
                        List<File> clientList = new ArrayList<>();
                        clientList.addAll(Arrays.asList(dirClient.listFiles()));
                        String pathServer = message.getUser().getFolderOnServer().getAbsolutePath();
                        if (client.getDefaultFolderOnServerSide() == null) {
                            File temp = new File(pathServer);
                            client.setDefaultFolderOnServerSide(temp);
                            client.setCurrentFolderForClientOnServer(temp);
                        }
                        File dirServer = new File(pathServer);
                        List<File> serverList = new ArrayList<>();
                        serverList.addAll(Arrays.asList(dirServer.listFiles()));
                        windowsManager.getMainGUIController().getConnectToServerButton().setText("Выйти с сервера");
                        windowsManager.getMainGUIController().clientListView.getItems().clear();
                        windowsManager.getMainGUIController().serverListView.getItems().clear();
                        windowsManager.getMainGUIController().clientListView.getItems().addAll(clientList);
                        windowsManager.getMainGUIController().serverListView.getItems().addAll(serverList);
                    });
                }
                break;
                case DOWNLOADING_FROM_SERVER: {
                    FileContent content = message.getFileContent();
                    try (final RandomAccessFile accessFile = new RandomAccessFile(message.getPathForDownloading() + "\\" + message.getFileForDownloading().getName(), "rw")) {
                        accessFile.seek(content.getStartPosition());
                        accessFile.write(content.getContent());
                        if (content.isLast()) {
                            System.out.println("File was copy.");
                            Platform.runLater(() -> {
                                windowsManager.getMainGUIController().clientListToRefresh(message);
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
                case DOWNLOADING_FROM_CLIENT: {
                    if (accessFile == null) {
                        accessFile = new RandomAccessFile(message.getFileForDownloading(), "r");
                        sendFile(ctx);
                    }

                }
                break;
                case FILE_WAS_SENT: {
                    Platform.runLater(() -> {
                                windowsManager.getMainGUIController().serverListToRefresh(message);
                            }
                    );
                }
            }
        }
    }

    private void sendFile(ChannelHandlerContext ctx) throws IOException {
        if (accessFile != null) {
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
            content.setContent(fileContent);
            boolean last = accessFile.getFilePointer() == accessFile.length();
            content.setLast(last);
            message.setFileContent(content);
            ctx.channel().writeAndFlush(message).addListener(future -> {
                if (!last) {
                    sendFile(ctx);
                }
            });
            if (last) {
                accessFile.close();
                accessFile = null;
            }
        }
    }
}
