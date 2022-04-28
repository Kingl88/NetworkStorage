package ru.gb.network_storage.client;

import io.netty.channel.*;
import message.*;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;
import ru.gb.network_storage.client.netty.ClientConnect;

import java.io.File;

public class Client {

    private final File DEFAULT_FOLDER_ON_CLIENT_SIDE = new File("NetworkStorage");
    private File defaultFolderOnServerSide;
    private File currentFolderOnClientSide;
    private File currentFolderForClientOnServer;

    private ChannelHandlerContext ctx;
    private final WindowsManager windowsManager = WindowsManager.getInstance();

    public WindowsManager getWindowsManager() {
        return windowsManager;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void start() throws InterruptedException {
        new ClientConnect(this).start();
    }

    public void attemptAuthorisation(String login, String password) {
        ctx.writeAndFlush(new AuthMessage(login, password));
    }
    public File getCurrentFolderOnClientSide() {
        return currentFolderOnClientSide;
    }

    public void setCurrentFolderOnClientSide(File currentFolderOnClientSide) {
        this.currentFolderOnClientSide = currentFolderOnClientSide;
    }

    public File getCurrentFolderForClientOnServer() {
        return currentFolderForClientOnServer;
    }

    public void setCurrentFolderForClientOnServer(File currentFolderForClientOnServer) {
        this.currentFolderForClientOnServer = currentFolderForClientOnServer;
    }

    public File getDEFAULT_FOLDER_ON_CLIENT_SIDE() {
        return DEFAULT_FOLDER_ON_CLIENT_SIDE;
    }

    public File getDefaultFolderOnServerSide() {
        return defaultFolderOnServerSide;
    }

    public void setDefaultFolderOnServerSide(File defaultFolderOnServerSide) {
        this.defaultFolderOnServerSide = defaultFolderOnServerSide;
    }
}