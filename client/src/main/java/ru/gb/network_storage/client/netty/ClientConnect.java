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
import ru.gb.network_storage.client.controller.Client;
import ru.gb.network_storage.client.netty.handler.ClientHandler;

import java.net.ConnectException;

public class ClientConnect {

    private Client client;

    public ClientConnect(final Client client) {
        this.client = client;
    }

    public void start() throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new ClientHandler(client));
                        }
                    });
            Channel channel = bootstrap.connect(client.getIpHost(), client.getPort()).sync().channel();
            channel.closeFuture().sync();
            client.setConnectError(false);
        }
        catch (Exception e){
            System.out.println("ERROR");
            client.setConnectError(true);
        }
        finally {
            workerGroup.shutdownGracefully();
        }
    }
}
