package ru.gb.network_storage.client.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class RegistrationController {
    public VBox reg;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button BtnOk;
    private MainGUIController backController;
    @FXML
    public void onRegistrationClick(ActionEvent event) {
        registration();
    }

    private void registration() {
        backController.registrationUser(username.getText(), password.getText());
    }

    public void closeWindow(){
        reg.getScene().getWindow().hide();

    }
    public void setBackController(final MainGUIController backController) {
        this.backController = backController;
    }
    //метод отслеживающий нажатие кнопки "Enter" в окне авторизации
    public void pressEnter(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            registration();
        }
    }


}
