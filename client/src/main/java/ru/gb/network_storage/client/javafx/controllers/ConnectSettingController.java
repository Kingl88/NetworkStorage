package ru.gb.network_storage.client.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectSettingController implements Initializable {
    public TextField ipAddress;
    public TextField port;
    public Button okButton;
    public VBox setting;
    private MainGUIController backController;

    public void setBackController(MainGUIController backController) {
        this.backController = backController;
    }

    public void onOkButton(ActionEvent event) {

        if(!ipAddress.getText().isEmpty() || !port.getText().isEmpty()) {
            backController.getClient().setIpHost(ipAddress.getText());
            backController.getClient().setPort(Integer.parseInt(port.getText()));
            setting.getScene().getWindow().hide();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Fill in the empty fields");
            alert.setHeaderText("Empty IP-address or port field");
            alert.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ipAddress.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if (!ipAddress.getText().matches("([1-2]\\d{1,2})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})")) {
                    ipAddress.setText("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You have entered an incorrect IP address");
                    alert.setHeaderText("Enter the correct IP address");
                    alert.show();
                }
            }
        });
        port.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if (!ipAddress.getText().matches("\\d{1,4}")) {
                    port.setText("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You have entered an incorrect number of port");
                    alert.setHeaderText("Enter the correct port");
                    alert.show();
                }
            }
        });
        ipAddress.setTooltip(new Tooltip("For example 127.0.0.1"));
        port.setTooltip(new Tooltip("For example 9000"));
    }
}
