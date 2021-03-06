package message;

public class AuthMessage extends Message {
    private String login;
    private String password;

    public AuthMessage() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

}
