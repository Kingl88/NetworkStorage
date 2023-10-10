package message;

import entity.Command;
import entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
@Data
@NoArgsConstructor
public class CommandMessage extends Message {
    private Path pathForDownloading;
    private File fileForDownloading;
    private Command command;
    private User user;
    FileContent fileContent;

    public CommandMessage(Command command) {
        this.command = command;
    }

}
