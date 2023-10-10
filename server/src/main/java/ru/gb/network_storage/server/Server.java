package ru.gb.network_storage.server;

import db.DBConnection;
import lombok.extern.slf4j.Slf4j;
import ru.gb.network_storage.server.handler.AuthorisationHandler;
import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.network_storage.server.handler.ServerHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
@Slf4j
public class Server {
    private final Connection connection = DBConnection.getConnection();


    private final Statement statement = connection.createStatement();


    private final int port;

    public static void main(String[] args) throws InterruptedException, SQLException {
        new Server(9000).start();
    }

    public Server(int port) throws SQLException {
        this.port = port;
    }

    public void start() throws InterruptedException, SQLException {
        createDateBase();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch){
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new AuthorisationHandler(),
                                    new ServerHandler());
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = server.bind(port).sync();
            log.info("Server started");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void createDateBase() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT," +
                "password TEXT" + ")");
    }
}
