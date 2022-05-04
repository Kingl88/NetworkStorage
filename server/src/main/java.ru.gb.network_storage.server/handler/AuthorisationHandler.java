package handler;

import entity.Command;
import message.CommandMessage;
import message.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import message.AuthMessage;
import message.TextMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AuthorisationHandler extends ChannelInboundHandlerAdapter {
    private List<User> users = new ArrayList<>();

    {
        User user = new User();
        user.setUsername("Ivan");
        user.setPassword("1234");
        users.add(user);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        System.out.println("From client");
        if (msg instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) msg;
            User user = new User();
            user.setUsername(authMessage.getLogin());
            user.setPassword(authMessage.getPassword());
            TextMessage textMessage = new TextMessage();
            if (users.contains(user)) {
                System.out.println("Пользователь авторизован");
                CommandMessage message = new CommandMessage();
                message.setCommand(Command.AUTHORIZATION_CONFIRMED);
                message.setUser(user);
                File folderForClient = new File("folderFor" + user.getUsername());
                if (!folderForClient.exists()) {
                    if (folderForClient.mkdir()) {
                        System.out.println("Created folder for user: " + user.getUsername());
                        message.getUser().setFolderOnServer(folderForClient);
                    }
                } else {
                    message.getUser().setFolderOnServer(folderForClient);
                    System.out.println("Name folder for user " + user.getUsername() + " is " + folderForClient.getName());
                }
                ctx.writeAndFlush(message);
            } else {
                System.out.println("Пользователь не авторизован");
                textMessage.setText("User wasn't logged");
                ctx.writeAndFlush(textMessage);
            }
        }
        ctx.fireChannelRead(msg);
    }
}
