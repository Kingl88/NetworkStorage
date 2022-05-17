package main.java.ru.gb.network_storage.server;

import db.DBConnection;
import handler.AuthorisationHandler;
import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import main.java.ru.gb.network_storage.server.handler.ServerHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);//пул потоков/thread, который будет управлять новыми подключениями
        // при появлении нового соединения, инициализирует его и прикрепляет к какому-то worker
        // отлавливает OP_ACCEPT и инициализирует новое подключение новым select в другом потоке.
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();//пул потоков который обрабатывает все остальные события приходящие от клиентов подключенных к серверу.
        try {
            ServerBootstrap server = new ServerBootstrap();//создаем конфигуратор сервера.
            server//конфигурируем сервер.
                    .group(bossGroup, workerGroup)//настраиваем группы (пулы потоков которые будут обрабатывать запросы)
                    .channel(NioServerSocketChannel.class)//выбираем тип канала который будет поддерживать сервер.
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {//процедура создания нового клиента, создание нового клиентского канала
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(//последовательность действие которые необходимо выполнить (некий алгоритм)
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),//первые 3 байта в сообщении кодирует длину сообщения, так же первых 3 байта отсекаются после прочтения сообщения.
                                    new LengthFieldPrepender(3),//дописывает информацию каким будет длина сообщения (добавляет в начало три байта, в которых записана длина сообщения)
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new AuthorisationHandler(),
                                    new ServerHandler());
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = server.bind(port).sync();
            System.out.println("Server started");
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
