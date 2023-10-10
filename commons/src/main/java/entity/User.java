package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Objects;

@Data
@NoArgsConstructor
public class User {
    private String username;
    private String password;
    private File folderOnServer;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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
