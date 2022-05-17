package ru.gb.network_storage.client.controller;

import entity.Command;
import io.netty.channel.*;
import message.*;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;
import ru.gb.network_storage.client.netty.ClientConnect;

import java.io.File;

public class Client {
    //IP адрес сервера по умолчанию
    private final String DEFAULT_IP_HOST = "localhost";
    //номер порта по умолчанию
    private final int DEFAULT_PORT = 9000;
    //IP-адрес сервера
    private String ipHost = DEFAULT_IP_HOST;
    //номер порта
    private int port = DEFAULT_PORT;
    //папка на стороне клиента, которая будет загружена при авторизации
    private final File DEFAULT_FOLDER_ON_CLIENT_SIDE = new File("NetworkStorage");
    //папка на стороне сервера, передается при ответе с сервера при успешной авторизации
    private File defaultFolderOnServerSide;
    //текущая папка на стороне клиента, в которой находится клиент
    private File currentFolderOnClientSide;
    //текущая папка на стороне сервера, в которой находится клиент
    private File currentFolderForClientOnServer;
    //хранится значение о том запущен сервер или нет
    private boolean startedServer;
    //контекст
    private ChannelHandlerContext ctx;
    //объект менеджера окно приложения
    private final WindowsManager windowsManager = WindowsManager.getInstance();
    //проверка на подключение к серверу
    private boolean connectError;

    public boolean isConnectError() {
        return connectError;
    }

    public void setConnectError(boolean error) {
        this.connectError = error;
    }

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

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public boolean isStartedServer() {
        return startedServer;
    }

    public void setStartedServer(boolean startedServer) {
        this.startedServer = startedServer;
    }

    public void disconnectClient() {
        if (ctx != null && !ctx.isRemoved()) {
            ctx.writeAndFlush(new CommandMessage(Command.CLIENT_DISCONNECT));
            //Пауза, что бы запрос успел обработаться сервером.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}