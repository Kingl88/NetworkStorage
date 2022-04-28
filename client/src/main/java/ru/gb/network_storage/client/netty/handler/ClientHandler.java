package ru.gb.network_storage.client.netty.handler;

import javafx.application.Platform;
import message.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.Message;
import message.TextMessage;
import ru.gb.network_storage.client.Client;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final Client client;
    ChannelHandlerContext ctx;

    public ClientHandler(final Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        this.ctx = ctx;
        client.setCtx(ctx);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println(message.getText());
        }
        if (msg instanceof CommandMessage) {
            CommandMessage message = (CommandMessage) msg;
            if (message.isConnectActive() && message.getUser() == null) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        client.getWindowsManager().getMainGUIController().labelServer.setText("Server online");
                    }
                });
            }
            if (message.getUser() != null && message.isConnectActive() && message.getUser().isLogIn()) {
                System.out.println("Client active");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        client.getWindowsManager().getAuthController().closeWindow();
                        client.getWindowsManager().getMainGUIController().labelClient.setText("Client connected");
                        client.getWindowsManager().getMainGUIController().serverListView.setVisible(true);
                        String pathClient = client.getDEFAULT_FOLDER_ON_CLIENT_SIDE().getAbsolutePath();
                        File dirClient = new File(pathClient); //path указывает на директорию
                        client.setCurrentFolderOnClientSide(dirClient);
                        List<File> clientList = new ArrayList<>();
                        clientList.addAll(Arrays.asList(dirClient.listFiles()));
                        String pathServer = message.getPathOnServer().getAbsolutePath();
                        if (client.getDefaultFolderOnServerSide() == null) {
                            File temp = new File(pathServer);
                            client.setDefaultFolderOnServerSide(temp);
                            client.setCurrentFolderForClientOnServer(temp);
                        }
                        File dirServer = new File(pathServer); //path указывает на директорию
                        List<File> serverList = new ArrayList<>();
                        serverList.addAll(Arrays.asList(dirServer.listFiles()));

                        client.getWindowsManager().getMainGUIController().clientListView.getItems().clear();
                        client.getWindowsManager().getMainGUIController().clientListView.getItems().addAll(clientList);
                        client.getWindowsManager().getMainGUIController().serverListView.getItems().addAll(serverList);
                    }
                });

            }
        }
    }
}
