package ru.gb.network_storage.client.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.gb.network_storage.client.Client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {
    @FXML
    public Button backToRootDirectoryClient, backToPreviousDirectoryClient, sendFileToServer,
            backToRootDirectoryCServer, backToPreviousDirectoryServer, sendFileToClient;
    @FXML
    public ListView<File> clientListView, serverListView;
    @FXML
    public Label labelClient;
    public Label labelServer;
    @FXML
    private MenuItem connectToServerButton;

    private WindowsManager windowsManager;
    ;

    public Client getClient() {
        return client;
    }

    private Client client;

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        client = new Client();
        windowsManager = WindowsManager.getInstance();
        windowsManager.init(this);

    }

    @FXML
    private void onConnectToServerButtonClick() throws Exception {
        new Thread(() -> {
            try {
                //запускаем логику клиента облачного хранилища
                client.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        windowsManager.openAuthorisationWindow();
    }

    public void attemptAuthorisation(String login, String password){
        client.attemptAuthorisation(login, password);
    }
}
