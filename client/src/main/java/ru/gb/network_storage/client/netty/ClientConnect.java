package ru.gb.network_storage.client.netty;

import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.network_storage.client.Client;
import ru.gb.network_storage.client.netty.handler.ClientHandler;

public class ClientConnect {
    //константа IP адреса сервера
    private final String IP_HOST ="localhost";
    //константа порта сервера
    private final int PORT = 9000;
    //
    private Client client;

    public ClientConnect(final Client client) {
        this.client = client;
    }
    public void start() throws InterruptedException {
        //пул потоков для обработки данных
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //настраиваем клиент перед запуском
            Bootstrap bootstrap = new Bootstrap();
            //workerGroup отвечает за соединение и за обмен данными
            bootstrap.group(workerGroup)
                    //используем класс NioSocketChannel для создания канала
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    //максимальный размер сообщения равен 1024*1024 байт, в начале сообщения для хранения длины зарезервировано 3 байта,
                                    //которые отбросятся после получения всего сообщения и передачи его дальше по цепочке
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    //Перед отправкой добавляет в начало сообщение 3 байта с длиной сообщения
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new ClientHandler(client));
                        }
                    });
            System.out.println("ru.gb.network_storage.client.Client started");

            Channel channel = bootstrap.connect(IP_HOST, PORT).sync().channel();

            channel.closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
        }
    }
}
