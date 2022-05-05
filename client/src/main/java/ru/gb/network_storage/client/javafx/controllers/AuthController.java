package ru.gb.network_storage.client.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class AuthController {
    public VBox auth;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button BtnOk;
    private MainGUIController backController;
    @FXML
    public void onAuthorizationBtnClick(ActionEvent event) throws InterruptedException {
        authorization();
    }

    private void authorization() {
        backController.attemptAuthorisation(username.getText(), password.getText());
    }

    public void closeWindow(){
        auth.getScene().getWindow().hide();

    }
    public void setBackController(final MainGUIController backController) {
        this.backController = backController;
    }
    //метод отслеживающий нажатие кнопки "Enter" в окне авторизации
    public void pressEnter(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            authorization();
        }
    }
}
