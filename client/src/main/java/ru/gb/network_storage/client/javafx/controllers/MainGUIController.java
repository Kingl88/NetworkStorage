package ru.gb.network_storage.client.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.gb.network_storage.client.Client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private Client client;

    public Client getClient() {
        return client;
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        client = new Client();
        windowsManager = WindowsManager.getInstance();
        windowsManager.init(this);
        clientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serverListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sendFileToServer.setOnAction(event -> {
            File file = clientListView.getSelectionModel().getSelectedItem();
            System.out.println(file);
        });
        clientListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                File file = clientListView.getSelectionModel().getSelectedItem();
                System.out.println("After doubleClick " + file.getAbsolutePath());
                client.setCurrentFolderOnClientSide(file);
                if (file.isDirectory()) {
                    clientListView.getItems().clear();
                    List<File> clientList = new ArrayList<>();
                    clientList.addAll(Arrays.asList(file.listFiles()));
                    clientListView.getItems().addAll(clientList);
                }
            }
        });
        serverListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                File file = serverListView.getSelectionModel().getSelectedItem();
                System.out.println("After doubleClick " + file.getAbsolutePath());
                client.setCurrentFolderForClientOnServer(file);
                if (file.isDirectory()) {
                    serverListView.getItems().clear();
                    List<File> clientList = new ArrayList<>();
                    clientList.addAll(Arrays.asList(file.listFiles()));
                    serverListView.getItems().addAll(clientList);
                }
            }
        });
        backToPreviousDirectoryClient.setOnAction(event -> {
            File parentFolder = client.getCurrentFolderOnClientSide().getParentFile();
            client.setCurrentFolderOnClientSide(parentFolder);
            System.out.println("After buttonUp click " + parentFolder.getAbsolutePath());
            clientListView.getItems().clear();
            List<File> clientList = new ArrayList<>();
            clientList.addAll(Arrays.asList(parentFolder.listFiles()));
            clientListView.getItems().addAll(clientList);
        });
        backToPreviousDirectoryServer.setOnAction(event -> {
            File parentFolder = client.getCurrentFolderForClientOnServer().getParentFile();
            System.out.println("Parent folder on server " + client.getCurrentFolderForClientOnServer().getAbsolutePath());
            System.out.println(client.getCurrentFolderForClientOnServer());
            System.out.println(client.getDefaultFolderOnServerSide());
            System.out.println(client.getCurrentFolderForClientOnServer().equals(client.getDefaultFolderOnServerSide()));
            if(!client.getCurrentFolderForClientOnServer().equals(client.getDefaultFolderOnServerSide())){
                client.setCurrentFolderForClientOnServer(parentFolder);
                System.out.println("After buttonUp click " + parentFolder.getAbsolutePath());
                serverListView.getItems().clear();
                List<File> clientList = new ArrayList<>();
                clientList.addAll(Arrays.asList(parentFolder.listFiles()));
                serverListView.getItems().addAll(clientList);
            } else{
                System.out.println("After buttonUp click " + client.getDefaultFolderOnServerSide().getAbsolutePath());
                serverListView.getItems().clear();
                List<File> clientList = new ArrayList<>();
                clientList.addAll(Arrays.asList(client.getDefaultFolderOnServerSide().listFiles()));
                serverListView.getItems().addAll(clientList);
            }

        });

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

    public void attemptAuthorisation(String login, String password) {
        client.attemptAuthorisation(login, password);
    }
}
