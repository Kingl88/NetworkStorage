package message;

import java.io.File;

public class CommandMessage extends Message {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    private File pathOnServer;
    private File pathToClient;

    public File getPathOnServer() {
        return pathOnServer;
    }

    public void setPathOnServer(final File pathOnServer) {
        this.pathOnServer = pathOnServer;
    }

    public File getPathToClient() {
        return pathToClient;
    }

    public void setPathToClient(final File pathToClient) {
        this.pathToClient = pathToClient;
    }

    public boolean isConnectActive() {
        return isConnectActive;
    }

    public void setConnectActive(final boolean connectActive) {
        isConnectActive = connectActive;
    }

    private static boolean isConnectActive;
}
