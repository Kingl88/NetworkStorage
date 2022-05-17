package db.services;

import entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthenticationService {
    private Statement statement;

    public AuthenticationService(Statement statement) {
        this.statement = statement;
    }
    public boolean isExists(String login, String password) {
        ResultSet resultSet;
        User user = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM users WHERE login = " +
                    "'" + login + "' AND password = '" +
                    password + "';");
            user = new User(resultSet.getString("login"), resultSet.getString("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user != null;
    }
}
