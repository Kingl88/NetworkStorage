package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private final int SIZE_BUF = 64 * 1024;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        CommandMessage message = new CommandMessage();
        message.setConnectActive(true);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if(msg instanceof AuthMessage){
            AuthMessage authMessage = (AuthMessage) msg;
            System.out.println(authMessage.getPassword());
        }

        if (msg instanceof FileRequestMessage) {
            FileRequestMessage frm = (FileRequestMessage) msg;
            final File file = new File(frm.getPath());
            try (final RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
                while (accessFile.getFilePointer() != accessFile.length()) {
                    byte[] fileContent;
                    long available = accessFile.length() - accessFile.getFilePointer();
                    if (available > SIZE_BUF) {
                        fileContent = new byte[SIZE_BUF];
                    } else {
                        fileContent = new byte[(int) available];
                    }
                    final FileContentMessage message = new FileContentMessage();
                    message.setStartPosition(accessFile.getFilePointer());
                    accessFile.read(fileContent);
                    message.setContent(fileContent);
                    message.setLast(accessFile.getFilePointer() == accessFile.length());
                    ctx.writeAndFlush(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("client disconnect");
    }
}
