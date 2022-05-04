package message;

import entity.Command;

import java.io.File;
import java.nio.file.Path;

public class CommandMessage extends Message {
    private Path pathForDownloading;
    private File fileForDownloading;
    private Command command;
    private User user;
    FileContent fileContent;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }

    public Path getPathForDownloading() {
        return pathForDownloading;
    }

    public void setPathForDownloading(Path pathForDownloading) {
        this.pathForDownloading = pathForDownloading;
    }

    public File getFileForDownloading() {
        return fileForDownloading;
    }

    public void setFileForDownloading(File fileForDownloading) {
        this.fileForDownloading = fileForDownloading;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
