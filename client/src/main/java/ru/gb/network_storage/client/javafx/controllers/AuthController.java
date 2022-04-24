package ru.gb.network_storage.client.javafx.controllers;

import io.netty.channel.ChannelHandlerContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import message.AuthMessage;
import ru.gb.network_storage.client.netty.ClientConnect;

public class AuthController {
    public VBox auth;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button BtnOk;
    private MainGUIController backController;
    private ClientConnect connect;
    private ChannelHandlerContext ctx;

    public void setCtx(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @FXML
    public void onAuthorizationBtnClick(ActionEvent event) throws InterruptedException {
        backController.attemptAuthorisation(username.getText(), password.getText());
    }
    public void closeWindow(){
        auth.getScene().getWindow().hide();

    }

    public void setBackController(final MainGUIController backController) {
        this.backController = backController;
    }
}
