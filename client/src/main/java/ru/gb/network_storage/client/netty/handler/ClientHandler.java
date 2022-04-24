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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                        String path = message.getPatToClient().getAbsolutePath();
                        //Если поставить в параметр к File путь "path", то не работает ничего.
                        File dirClient = new File("C:\\Users\\siarh\\IdeaProjects\\NetworkStorage\\clientStorage"); //path указывает на директорию
                        List<File> clientList = new ArrayList<>();
                        for (File file : dirClient.listFiles()) {
                            if (file.isFile())
                                clientList.add(file);
                        }
                        File dirServer = new File("C:\\Users\\siarh\\IdeaProjects\\NetworkStorage\\serverStorage"); //path указывает на директорию
                        List<File> serverList = new ArrayList<>();
                        for (File file : dirServer.listFiles()) {
                            if (file.isFile())
                                serverList.add(file);
                        }

                        client.getWindowsManager().getMainGUIController().clientListView.getItems().clear();
                        client.getWindowsManager().getMainGUIController().clientListView.getItems().addAll(clientList);
                        client.getWindowsManager().getMainGUIController().serverListView.getItems().addAll(serverList);
                    }
                });

            }
        }
//        if (msg instanceof CommandMessage) {
//            CommandMessage message = (CommandMessage) msg;
//            if (message.isConnectActive() && message.getUser() == null) {
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        client.getWindowsManager().getMainGUIController().labelServer.setText("Server online");
//                    }
//                });
//            }
//            if (message.getUser() != null && message.isConnectActive() && message.getUser().isLogIn()) {
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        client.getWindowsManager().getAuthController().closeWindow();
//                        client.getWindowsManager().getMainGUIController().labelClient.setText("Client connected");
//                        client.getWindowsManager().getMainGUIController().serverListView.setVisible(true);
//                        File dir = new File(message.getPatToClient()); //path указывает на директорию
//                        List<File> lst = new ArrayList<>();
//                        for (File file : dir.listFiles()) {
//                            if (file.isFile())
//                                lst.add(file);
//                        }
//                        client.getWindowsManager().getMainGUIController().clientListView.getItems().clear();
//                        client.getWindowsManager().getMainGUIController().clientListView.getItems().addAll(lst);
//                    }
//                });
//
//            }
//        }
    }
}
