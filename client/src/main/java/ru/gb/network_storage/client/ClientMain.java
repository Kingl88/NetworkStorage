package ru.gb.network_storage.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.gb.network_storage.client.javafx.controllers.MainGUIController;

import java.net.URL;

public class ClientMain extends Application {
    private MainGUIController controller;

    public static void main(String[] args) {
        Application.launch(args);
    }
    @Override
    public void start(final Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/MainClientForm.fxml"));
        controller = loader.getController();
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (controller.getClient() != null) {
                controller.getClient().disconnectClient();
            }
            //сворачиваем окно
            Platform.exit();
            //указываем системе, что выход без ошибки
            System.exit(0);
        });
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
