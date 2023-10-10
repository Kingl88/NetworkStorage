package ru.gb.network_storage.client.controller;

import entity.Command;
import io.netty.channel.*;
import lombok.Data;
import message.*;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;
import ru.gb.network_storage.client.netty.ClientConnect;

import java.io.File;

@Data
public class Client {
    private final String DEFAULT_IP_HOST = "localhost";
    private final int DEFAULT_PORT = 9000;
    private String ipHost = DEFAULT_IP_HOST;
    private int port = DEFAULT_PORT;
    private final File DEFAULT_FOLDER_ON_CLIENT_SIDE = new File("NetworkStorage");
    private File defaultFolderOnServerSide;
    private File currentFolderOnClientSide;
    private File currentFolderForClientOnServer;
    private boolean startedServer;
    private ChannelHandlerContext ctx;
    private final WindowsManager windowsManager = WindowsManager.getInstance();
    private boolean connectError;

    public void start() throws InterruptedException {
        new ClientConnect(this).start();
    }
    public void attemptAuthorisation(String login, String password) {
        ctx.writeAndFlush(new AuthMessage(login, password));
    }

    public void disconnectClient() {
        if (ctx != null && !ctx.isRemoved()) {
            ctx.writeAndFlush(new CommandMessage(Command.CLIENT_DISCONNECT));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}