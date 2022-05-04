package handler;

import entity.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private final int SIZE_BUF = 64 * 1024;
    private CommandMessage message;
    private RandomAccessFile accessFile;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        CommandMessage message = new CommandMessage();
        message.setCommand(Command.CHANNEL_HAS_BEEN_ACTIVATED);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg instanceof CommandMessage) {
            message = (CommandMessage) msg;
            switch (message.getCommand()) {
                case DOWNLOADING_FROM_CLIENT: {
                    FileContent content = message.getFileContent();
                    if (content == null) {
                        ctx.writeAndFlush(message);
                    } else {
                        accessFile = new RandomAccessFile(message.getPathForDownloading() + "\\" + message.getFileForDownloading().getName(), "rw");
                        accessFile.seek(content.getStartPosition());
                        accessFile.write(content.getContent());
                        if (content.isLast()) {
                            message.setCommand(Command.FILE_WAS_SENT);
                            ctx.writeAndFlush(message);
                            System.out.println("File was copy.");
                            accessFile.close();
                            accessFile = null;
                        }
                    }
                }
                break;
                case DOWNLOADING_FROM_SERVER: {
                    if (accessFile == null) {
                        accessFile = new RandomAccessFile(message.getFileForDownloading(), "r");
                        sendFile(ctx);
                    }
                }
                break;
            }
        }
    }

    private void sendFile(ChannelHandlerContext ctx) throws IOException {
        if (accessFile != null) {
            byte[] fileContent;
            long available = accessFile.length() - accessFile.getFilePointer();
            if (available > SIZE_BUF) {
                fileContent = new byte[SIZE_BUF];
            } else {
                fileContent = new byte[(int) available];
            }
            FileContent content = new FileContent();
            content.setStartPosition(accessFile.getFilePointer());
            accessFile.read(fileContent);
            content.setContent(fileContent);
            boolean last = accessFile.getFilePointer() == accessFile.length();
            content.setLast(last);
            message.setFileContent(content);
            ctx.channel().writeAndFlush(message).addListener(future -> {
                if (!last) {
                    sendFile(ctx);
                }
            });
            if (last) {
                accessFile.close();
                accessFile = null;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("client disconnect");
        if (accessFile != null) {
            accessFile.close();
        }
    }
}
