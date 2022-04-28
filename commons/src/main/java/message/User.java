package message;

import java.util.Objects;

public class User {
    private String username;
    private String password;

    private boolean isLogIn;

    public boolean isLogIn() {
        return isLogIn;
    }

    public void setLogIn(final boolean logIn) {
        isLogIn = logIn;
    }

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

    public String getPassword() {
        return password;
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
