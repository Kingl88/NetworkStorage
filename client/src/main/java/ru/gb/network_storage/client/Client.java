package ru.gb.network_storage.client;

import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import message.*;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;
import ru.gb.network_storage.client.netty.ClientConnect;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Client {
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
}