package handler;

import db.DBConnection;
import db.services.AuthenticationService;
import entity.Command;
import message.CommandMessage;
import entity.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import message.AuthMessage;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class AuthorisationHandler extends ChannelInboundHandlerAdapter {
   private AuthenticationService service;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
       Connection connection = DBConnection.getConnection();
       Statement statement = connection.createStatement();
       service = new AuthenticationService(statement);
        System.out.println("From client");
        if (msg instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) msg;
            if (service.isExists(authMessage.getLogin(), authMessage.getPassword())) {
                System.out.println("Пользователь авторизован");
                CommandMessage message = new CommandMessage();
                message.setCommand(Command.AUTHORIZATION_CONFIRMED);
                message.setUser(new User(authMessage.getLogin(), authMessage.getPassword()));
                File folderForClient = new File("folderFor" + authMessage.getLogin());
                if (!folderForClient.exists()) {
                    if (folderForClient.mkdir()) {
                        System.out.println("Created folder for user: " + authMessage.getLogin());
                        message.getUser().setFolderOnServer(folderForClient);
                    }
                } else {
                    message.getUser().setFolderOnServer(folderForClient);
                    System.out.println("Name folder for user " + authMessage.getLogin() + " is " + folderForClient.getName());
                }
                ctx.writeAndFlush(message);
            } else {
                CommandMessage message = new CommandMessage(Command.USER_NOT_FOUND_IN_DATABASE);
                ctx.writeAndFlush(message);
            }
        }
        ctx.fireChannelRead(msg);
    }
}
