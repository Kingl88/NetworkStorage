package ru.gb.network_storage.client.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ConnectSettingController {
    public TextField ipAddress;
    public TextField port;
    public Button okButton;
    public VBox setting;
    private MainGUIController backController;

    public void setBackController(MainGUIController backController) {
        this.backController = backController;
    }

    public void onOkButton(ActionEvent event) {
        backController.getClient().setIpHost(ipAddress.getText());
        backController.getClient().setPort(Integer.parseInt(port.getText()));
        setting.getScene().getWindow().hide();
    }
}
