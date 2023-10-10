package ru.gb.network_storage.client.javafx.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
@Getter
public class WindowsManager {
    private static final WindowsManager ownInstance = new WindowsManager();

    public static WindowsManager getInstance() {
        return ownInstance;
    }

    private MainGUIController mainGUIController;

    public void init(MainGUIController mainGUIController) {
        this.mainGUIController = mainGUIController;
    }

    private AuthController authController;
    private RegistrationController registrationController;


    private ConnectSettingController settingController;

    public void openAuthorisationWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuthForm.fxml"));
            Parent root = loader.load();
            authController = loader.getController();
            authController.setBackController(mainGUIController);
            stage.setTitle("Authorisation");
            stage.setScene(new Scene(root, 300, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openRegistrationWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResourceAsStream("/RegistrationForm.fxml"));
            registrationController = loader.getController();
            registrationController.setBackController(mainGUIController);
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, 300, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.setOnCloseRequest(event -> {
                System.out.println("Close");
                stage.hide();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openSettingConnect() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConnectSetting.fxml"));
            Parent root = loader.load();
            settingController = loader.getController();
            settingController.setBackController(mainGUIController);
            stage.setTitle("Authorisation");
            stage.setScene(new Scene(root, 600, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
