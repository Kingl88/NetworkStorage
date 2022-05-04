package ru.gb.network_storage.client.javafx.controllers;

import entity.Command;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import message.CommandMessage;
import ru.gb.network_storage.client.controller.Client;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
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
    public Label labelClient, labelServer;
    public MenuItem connectSetting;
    @FXML
    private MenuItem connectToServerButton;
    private WindowsManager windowsManager;
    private Client client;

    public Client getClient() {
        return client;
    }

    //метод для настройки некоторых свойств главного окна
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        client = new Client();
        windowsManager = WindowsManager.getInstance();
        windowsManager.init(this);
        clientListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        serverListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        propertyViewList(clientListView);
        propertyViewList(serverListView);
    }

    public MenuItem getConnectToServerButton() {
        return connectToServerButton;
    }

    //метод для настройки отображения информации в ListView
    private void propertyViewList(ListView<File> listView) {
        listView.setCellFactory(lv -> new ListCell<File>() {
            private ImageView imageView = new ImageView();

            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    // restore empty look of the cell
                    setText("");
                    setGraphic(null);
                } else {
                    if (item.isDirectory()) {
                        Image image = new Image("images/folder.png");
                        imageView.setFitWidth(15);
                        imageView.setFitHeight(15);
                        imageView.setImage(image);
                        setGraphic(imageView);
                        setText(item.getName());
                    } else {
                        Image image = new Image("images/file.png");
                        imageView.setFitWidth(15);
                        imageView.setFitHeight(15);
                        imageView.setImage(image);
                        setGraphic(imageView);
                        setText(item.getName());
                    }
                    // set cell contents based on item

                }
            }
        });
    }

    @FXML
    private void onConnectToServerButtonClick() throws Exception {
        System.out.println(client.isStartedServer());
        if (!client.isStartedServer()) {
            new Thread(() -> {
                try {
                    //запускаем логику клиента облачного хранилища
                    client.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            client.setStartedServer(true);
            windowsManager.openAuthorisationWindow();
        } else {
        }
    }

    public void attemptAuthorisation(String login, String password) {
        client.attemptAuthorisation(login, password);
    }

    /**
     * Метод отрабатывающий нажатие на кнопку "Up" в клиентской части
     * возвращение к предыдущей директории в окне клиента.
     */
    public void onBackToPreviousDirectoryClient(ActionEvent event) {
        File parentFolder = client.getCurrentFolderOnClientSide().getParentFile();
        client.setCurrentFolderOnClientSide(parentFolder);
        System.out.println("After buttonUp click on Client side " + parentFolder.getAbsolutePath());
        clientListView.getItems().clear();
        List<File> clientList = new ArrayList<>();
        clientList.addAll(Arrays.asList(parentFolder.listFiles()));
        clientListView.getItems().addAll(clientList);
    }

    /**
     * Метод отрабатывающий нажатие на кнопку "Up" в серверной части
     * возвращение к предыдущей директории в окне сервера.
     */
    public void onBackToPreviousDirectoryServer(ActionEvent event) {
        File parentFolder = client.getCurrentFolderForClientOnServer().getParentFile();
        if (!client.getCurrentFolderForClientOnServer().equals(client.getDefaultFolderOnServerSide())) {
            client.setCurrentFolderForClientOnServer(parentFolder);
            System.out.println("After buttonUp click on Server side " + parentFolder.getAbsolutePath());
            serverListView.getItems().clear();
            List<File> clientList = new ArrayList<>();
            clientList.addAll(Arrays.asList(parentFolder.listFiles()));
            serverListView.getItems().addAll(clientList);
        } else {
            System.out.println("After buttonUp click " + client.getDefaultFolderOnServerSide().getAbsolutePath());
            serverListView.getItems().clear();
            List<File> clientList = new ArrayList<>();
            clientList.addAll(Arrays.asList(client.getDefaultFolderOnServerSide().listFiles()));
            serverListView.getItems().addAll(clientList);
        }
    }

    /**
     * Метод отрабатывающий двойное нажатие клавиши в окне списка файлов сервера
     */
    public void onNextDirectoryOnServerSide(MouseEvent mouseEvent) {
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
    }

    /**
     * Метод отрабатывающий двойное нажатие клавиши в окне списка файлов клиента
     */
    public void onNextDirectoryOnClientSide(MouseEvent mouseEvent) {
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
    }

    /**
     * Метод отрабатывающий нажатия кнопки "Send" на ст0роне клиента
     */
    public void onSendFileToServer(ActionEvent event) {
        Path pathToDownload;
        if (serverListView.getSelectionModel().getSelectedItem() != null) {
            pathToDownload = serverListView.getSelectionModel().getSelectedItem().toPath();
            if (pathToDownload.toFile().isFile()) {
                pathToDownload = pathToDownload.getParent();
            }
        } else {
            pathToDownload = client.getCurrentFolderForClientOnServer().toPath();
        }
        File fileForDownloadFromServer = clientListView.getSelectionModel().getSelectedItem();
        if (fileForDownloadFromServer != null) {
            System.out.println(fileForDownloadFromServer.getAbsoluteFile());
            CommandMessage message = new CommandMessage();
            message.setCommand(Command.DOWNLOADING_FROM_CLIENT);
            message.setFileForDownloading(fileForDownloadFromServer);
            message.setPathForDownloading(pathToDownload);
            client.getCtx().writeAndFlush(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("123");
            alert.setContentText("Choose file for downloading");
            alert.setHeaderText("Client");
            alert.show();
        }
    }

    public void onSendFileToClient(ActionEvent actionEvent) {
        Path pathToDownload;
        if (clientListView.getSelectionModel().getSelectedItem() != null) {
            pathToDownload = clientListView.getSelectionModel().getSelectedItem().toPath();
            if (pathToDownload.toFile().isFile()) {
                pathToDownload = pathToDownload.getParent();
            }
        } else {
            pathToDownload = client.getCurrentFolderOnClientSide().toPath();
        }
        File fileForDownloadFromServer = serverListView.getSelectionModel().getSelectedItem();
        if (fileForDownloadFromServer != null) {
            System.out.println(fileForDownloadFromServer.getAbsoluteFile());
            CommandMessage message = new CommandMessage();
            message.setCommand(Command.DOWNLOADING_FROM_SERVER);
            message.setFileForDownloading(fileForDownloadFromServer);
            message.setPathForDownloading(pathToDownload);
            client.getCtx().writeAndFlush(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("123");
            alert.setContentText("Choose file for downloading");
            alert.setHeaderText("Client");
            alert.show();
        }
    }

    //метод для обновления ListView в окне клиента
    public void clientListToRefresh(CommandMessage message) {
        Path parentFolder = message.getPathForDownloading();
        System.out.println("Client " + parentFolder);
        clientListView.getItems().clear();
        List<File> clientList = new ArrayList<>();
        clientList.addAll(Arrays.asList(parentFolder.toFile().listFiles()));
        clientListView.getItems().addAll(clientList);
    }

    //метод для обновления ListView в окне сервера
    public void serverListToRefresh(CommandMessage message) {
        Path parentFolder = message.getPathForDownloading();
        System.out.println("Server " + parentFolder);
        serverListView.getItems().clear();
        List<File> serverList = new ArrayList<>();
        serverList.addAll(Arrays.asList(parentFolder.toFile().listFiles()));
        serverListView.getItems().addAll(serverList);
    }

    public void onConnectSetting(ActionEvent event) {
        windowsManager.openSettingConnect();
    }
}
