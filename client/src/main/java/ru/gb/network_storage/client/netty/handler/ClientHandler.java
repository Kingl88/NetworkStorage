package ru.gb.network_storage.client.netty.handler;

import entity.Command;
import javafx.application.Platform;
import message.AuthMessage;
import message.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.FileContent;
import message.Message;
import ru.gb.network_storage.client.controller.Client;
import ru.gb.network_storage.client.javafx.controllers.MainGUIController;
import ru.gb.network_storage.client.javafx.controllers.WindowsManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    private long startCoping;
    private final double BYTE_IN_MB = 1048576;
    private final double BYTE_IN_GB = 1073741824;
    private final int SIZE_BUF = 64 * 1024;
    private RandomAccessFile accessFile;
    private final Client client;
    private MainGUIController controller;
    private CommandMessage temp_message;
    private CommandMessage message;
    private final WindowsManager windowsManager = WindowsManager.getInstance();

    public ClientHandler(final Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        client.setCtx(ctx);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message msg) throws Exception {
        if (msg instanceof CommandMessage) {
            controller = windowsManager.getMainGUIController();
            message = (CommandMessage) msg;
            System.out.println(message.getCommand());
            switch (message.getCommand()) {
                case CHANNEL_HAS_BEEN_ACTIVATED: {
                    Platform.runLater(() -> {
                        client.getWindowsManager().getMainGUIController().labelServer.setText("Server online");
                    });
                }
                break;
                case AUTHORIZATION_CONFIRMED: {
                    Platform.runLater(() -> {
                        if (windowsManager.getAuthController() != null) {
                            windowsManager.getAuthController().closeWindow();
                        }
                        controller.labelClient.setText("Client connected");
                        controller.serverListView.setVisible(true);
                        String pathClient = client.getDEFAULT_FOLDER_ON_CLIENT_SIDE().getAbsolutePath();
                        File dirClient = new File(pathClient);
                        if(!dirClient.exists()){
                            dirClient.mkdir();
                        }
                        client.setCurrentFolderOnClientSide(new File(client.getDEFAULT_FOLDER_ON_CLIENT_SIDE().getAbsolutePath()));
                        List<File> clientList = new ArrayList<>();
                        clientList.addAll(Arrays.asList(dirClient.listFiles()));
                        String pathServer = message.getUser().getFolderOnServer().getAbsolutePath();
                        if (client.getDefaultFolderOnServerSide() == null) {
                            File temp = new File(pathServer);
                            client.setDefaultFolderOnServerSide(temp);
                            client.setCurrentFolderForClientOnServer(temp);
                        }
                        File dirServer = new File(pathServer);
                        List<File> serverList = new ArrayList<>();
                        serverList.addAll(Arrays.asList(dirServer.listFiles()));
                        controller.getConnectToServerButton().setText("Выйти с сервера");
                        controller.clientListView.getItems().clear();
                        controller.serverListView.getItems().clear();
                        controller.clientListView.getItems().addAll(clientList);
                        controller.sorted(controller.clientListView);
                        controller.serverListView.getItems().addAll(serverList);
                        controller.sorted(controller.serverListView);
                        controller.splitPain.setVisible(true);
                        controller.visibleSplitPain();
                        controller.connectSetting.setDisable(true);
                        controller.logOut.setVisible(true);
                        controller.registrationNewUser.setVisible(false);
                        controller.backToPreviousDirectoryServer.setVisible(true);
                        controller.backToPreviousDirectoryClient.setVisible(true);
                        controller.backToRootDirectoryClient.setVisible(true);
                        controller.backToRootDirectoryCServer.setVisible(true);
                        controller.sendFileToClient.setVisible(true);
                        controller.sendFileToServer.setVisible(true);
                        controller.clientListView.setVisible(true);
                    });
                }
                break;
                case DOWNLOADING_FROM_SERVER: {
                    String textInLabel = "Coping file from server: \"" + message.getFileForDownloading().getName() + "\" on client to folder: \"" + message.getPathForDownloading() + "\"";
                    double scale = Math.pow(10, 2);
                    double megaByteWasCopied = Math.ceil(scale * controller.progressBar.getProgress() * message.getFileContent().getFileLength() / BYTE_IN_MB) / scale;
                    double sizeFileInGb = Math.ceil(scale * message.getFileContent().getFileLength() / BYTE_IN_GB) / scale;
                    if (startCoping == 0) {
                        startCoping = System.currentTimeMillis();
                    }
                    long currentMillis = System.currentTimeMillis();
                    if (currentMillis - startCoping > 450) {
                        System.out.println(currentMillis - startCoping);
                        Platform.runLater(() -> {
                            controller.progressBar.setVisible(true);
                            controller.labelDownload.setVisible(true);
                            controller.labelInformation.setVisible(true);
                            controller.labelInformation.setText(textInLabel);
                            if (sizeFileInGb < 1) {
                                controller.labelDownload.setText("Downloaded: " + megaByteWasCopied + "Mb/" + Math.ceil(sizeFileInGb * 1024 * scale) / scale + "Mb");
                            } else {
                                if (megaByteWasCopied < 1000) {
                                    controller.labelDownload.setText("Downloaded: " + megaByteWasCopied + "Mb/" + sizeFileInGb + "Gb")
                                    ;
                                } else {
                                    controller.labelDownload.setText("Downloaded: " + Math.ceil(scale * megaByteWasCopied / 1024) / scale + "Gb/" + sizeFileInGb + "Gb");
                                }
                            }
                        });
                        startCoping = currentMillis;
                    }
                    FileContent content = message.getFileContent();
                    try (final RandomAccessFile accessFile = new RandomAccessFile(message.getPathForDownloading() + "\\" + message.getFileForDownloading().getName(), "rw")) {
                        if (controller.progressBar.getProgress() < 1) {
                            controller.progressBar.setProgress(controller.progressBar.getProgress() + SIZE_BUF / message.getFileContent().getFileLength());
                        }
                        accessFile.seek(content.getStartPosition());
                        accessFile.write(content.getContent());
                        if (content.isLast()) {
                            System.out.println("File was copy.");
                            startCoping = 0;
                            Platform.runLater(() -> {
                                controller.labelDownload.setVisible(false);
                                controller.progressBar.setProgress(0);
                                controller.progressBar.setVisible(false);
                                controller.labelInformation.setVisible(false);
                                if (temp_message != null) {
                                    System.out.println(temp_message.getPathForDownloading());
                                    controller.clientListToRefresh(temp_message);
                                } else {
                                    controller.clientListToRefresh(message);
                                }
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
                case DOWNLOADING_FROM_CLIENT: {
                    CommandMessage messageForRefresh = new CommandMessage();
                    if (message.getFileForDownloading().isDirectory()) {
                        messageForRefresh.setPathForDownloading(message.getPathForDownloading());
                        copyInformation(message.getFileForDownloading(), new File(message.getPathForDownloading().toFile().getAbsolutePath() + "/" + message.getFileForDownloading().getName()), ctx);
                        Platform.runLater(() -> {
                            controller.serverListToRefresh(messageForRefresh);
                        });
                    } else {
                        copyFile(message.getFileForDownloading(), message.getPathForDownloading(), ctx);
                        Platform.runLater(() -> {
                            controller.serverListToRefresh(message);
                        });
                    }
                }
                break;
                case REGISTRATION_CONFIRMED: {
                    System.out.println("Confirmed");
                    Platform.runLater(() -> {
                        windowsManager.getRegistrationController().closeWindow();
                    });
                    ctx.writeAndFlush(new AuthMessage(message.getUser().getUsername(), message.getUser().getPassword()));
                }
                break;
                case USER_NOT_FOUND_IN_DATABASE: {
                    Platform.runLater(() -> {
                        windowsManager.getAuthController().userNotFound();
                    });
                }
                break;
                case CREATE_DIRECTORY: {
                    System.out.println(message.getPathForDownloading());
                    if (!message.getPathForDownloading().toFile().exists()) {
                        message.getPathForDownloading().toFile().mkdir();
                        if (temp_message == null) {
                            temp_message = new CommandMessage();
                            temp_message.setPathForDownloading(message.getPathForDownloading().getParent());
                        }
                    }
                }
                break;
            }
        }
    }

    private void copyInformation(File source, File destination, ChannelHandlerContext ctx) throws IOException, InterruptedException {
        if (source.isDirectory()) {
            message.setPathForDownloading(destination.toPath());
            message.setCommand(Command.CREATE_DIRECTORY);
            ctx.writeAndFlush(message);
            List<File> list = Arrays.stream(Objects.requireNonNull(source.listFiles())).sorted((o1, o2) -> {
                if (o1.isDirectory() && o2.isFile()) {
                    return 1;
                } else if (o1.isFile() && o2.isDirectory()) {
                    return -1;
                } else {
                    return o1.getName().compareTo(o2.getName());

                }
            }).collect(Collectors.toList());
            List<String> sourceList = list.stream().map(File::getName).collect(Collectors.toList());
            for (String child : sourceList) {
                File newSource = new File(source, child);
                File newDestination = new File(destination, child);
                copyInformation(newSource, newDestination, ctx);
            }
        } else {
            copyFile(source, destination.getParentFile().toPath(), ctx);
        }
    }

    private void copyFile(File source, Path path, ChannelHandlerContext ctx) throws IOException {
        System.out.println("FILE copyFile" + source);
        System.out.println("Dir copyFile" + path);
        RandomAccessFile accessFile = new RandomAccessFile(source, "r");
        sendFile(accessFile, source, path, ctx);
    }

    private void sendFile(RandomAccessFile accessFile, File source, Path path, ChannelHandlerContext ctx) throws IOException {
        System.out.println("FILE sendFile" + source);
        System.out.println("Dir sendFile" + path);
        byte[] fileContent;
        long available = accessFile.length() - accessFile.getFilePointer();
        if (available > SIZE_BUF) {
            fileContent = new byte[SIZE_BUF];
        } else {
            fileContent = new byte[(int) available];
        }
        FileContent content = new FileContent();
        content.setStartPosition(accessFile.getFilePointer());
        accessFile.read(fileContent);
        content.setFileLength(accessFile.length());
        content.setContent(fileContent);
        boolean last = accessFile.getFilePointer() == accessFile.length();
        content.setLast(last);
        message.setFileContent(content);
        message.setFileForDownloading(source);
        message.setPathForDownloading(path);
        message.setCommand(Command.DOWNLOADING_FROM_CLIENT);
        RandomAccessFile finalAccessFile = accessFile;
        ctx.channel().writeAndFlush(message).addListener(future -> {
            if (!last) {
                sendFile(finalAccessFile, source, path, ctx);
            }
        });
        String folderOnServer = message.getPathForDownloading().toString().substring(message.getPathForDownloading().toString().indexOf(client.getDefaultFolderOnServerSide().getName()));
        String textInLabel = "Coping file from client: \"" + message.getFileForDownloading().getName() + "\" on server to folder: \"" + folderOnServer + "\"";
        double scale = Math.pow(10, 2);
        double megaByteWasCopied = Math.ceil(scale * controller.progressBar.getProgress() * message.getFileContent().getFileLength() / BYTE_IN_MB) / scale;
        double sizeFileInGb = Math.ceil(scale * message.getFileContent().getFileLength() / BYTE_IN_GB) / scale;
        if (startCoping == 0) {
            startCoping = System.currentTimeMillis();
        }
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - startCoping > 450) {
            Platform.runLater(() -> {
                controller.progressBar.setVisible(true);
                controller.labelDownload.setVisible(true);
                controller.labelInformation.setVisible(true);
                controller.labelInformation.setText(textInLabel);
                if (sizeFileInGb < 1) {
                    controller.labelDownload.setText("Downloaded: " + megaByteWasCopied + "Mb/" + Math.ceil(sizeFileInGb * 1024 * scale) / scale + "Mb");
                } else {
                    if (megaByteWasCopied < 1000) {
                        controller.labelDownload.setText("Downloaded: " + megaByteWasCopied + "Mb/" + sizeFileInGb + "Gb");
                    } else {
                        controller.labelDownload.setText("Downloaded: " + Math.ceil(scale * megaByteWasCopied / 1024) / scale + "Gb/" + sizeFileInGb + "Gb");
                    }
                }
            });
            startCoping = currentMillis;
        }
        if (controller.progressBar.getProgress() < 1) {
            controller.progressBar.setProgress(controller.progressBar.getProgress() + SIZE_BUF / message.getFileContent().getFileLength());
        }
        if (last) {
            startCoping = 0;
            accessFile.close();
            Platform.runLater(() -> {
                controller.labelDownload.setVisible(false);
                controller.progressBar.setProgress(0);
                controller.labelInformation.setVisible(false);
                controller.progressBar.setVisible(false);
            });
        }
    }
}
