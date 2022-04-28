package handler;

import message.CommandMessage;
import message.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
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
        try {
            AuthMessage authMessage = (AuthMessage) msg;
            User user = new User();
            user.setUsername(authMessage.getLogin());
            user.setPassword(authMessage.getPassword());
            TextMessage textMessage = new TextMessage();
            System.out.println(authMessage.getLogin());
            if(users.contains(user)){
                System.out.println("Пользователь авторизован");
                user.setLogIn(true);
                CommandMessage message = new CommandMessage();
                message.setConnectActive(ctx.channel().isActive());
                message.setUser(user);
                File folderForClient = new File("folderFor" + user.getUsername());
                if(!folderForClient.exists()){
                   if(folderForClient.mkdir()){
                       System.out.println("Created folder for user: " + user.getUsername());
                       message.setPathOnServer(folderForClient);
                   }
                } else {
                    message.setPathOnServer(folderForClient);
                    System.out.println("Name folder for user " + user.getUsername() + " is " + folderForClient.getName());
                }
                ctx.writeAndFlush(message);
            } else {
                System.out.println("Пользователь не авторизован");
                textMessage.setText("User wasn't logged");
                ctx.writeAndFlush(textMessage);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
