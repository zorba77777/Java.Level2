package ru.geekbrains.march.chat.server;

import java.sql.*;

public class DbAuthenticationProvider implements AuthenticationProvider {

    private static Connection connection;
    private static Statement stmt;

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE LOGIN = \"" + login + "\" AND PASSWORD = \"" + password + "\";")) {
            while (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        throw new UnsupportedOperationException();
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public static void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
