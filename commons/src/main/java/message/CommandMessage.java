package message;

import java.io.File;

public class CommandMessage extends Message {
    private final File DEFAULT_PATH_ON_SERVER = new File("storageServer");
    private final File DEFAULT_PATH_ON_CLIENT = new File("storageClient");
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    private File pathOnServer = DEFAULT_PATH_ON_SERVER;
    private File patToClient = DEFAULT_PATH_ON_CLIENT;

    public File getPathOnServer() {
        return pathOnServer;
    }

    public void setPathOnServer(final File pathOnServer) {
        this.pathOnServer = pathOnServer;
    }

    public File getPatToClient() {
        return patToClient;
    }

    public void setPatToClient(final File patToClient) {
        this.patToClient = patToClient;
    }

    public boolean isConnectActive() {
        return isConnectActive;
    }

    public void setConnectActive(final boolean connectActive) {
        isConnectActive = connectActive;
    }

    private boolean isConnectActive;
}
