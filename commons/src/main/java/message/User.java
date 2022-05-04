package message;

import java.io.File;
import java.util.Objects;

public class User {
    private String username;
    private String password;
    private File folderOnServer;

    public User() {
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public File getFolderOnServer() {
        return folderOnServer;
    }

    public void setFolderOnServer(File folderOnServer) {
        this.folderOnServer = folderOnServer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
