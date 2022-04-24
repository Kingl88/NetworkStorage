package ru.gb.network_storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ClientMain extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        URL xmlUrl = getClass().getResource("/MainClientForm.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
