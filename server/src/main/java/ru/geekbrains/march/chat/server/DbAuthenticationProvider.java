package ru.geekbrains.march.chat.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbAuthenticationProvider implements AuthenticationProvider {
    private DbConnection dbConnection;
    private static final Logger logger = LogManager.getLogger(DbAuthenticationProvider.class);

    @Override
    public void init() {
        dbConnection = new DbConnection();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        String query = String.format("select nickname from users where login = '%s' and password = '%s';", login, password);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        String query = String.format("update users set nickname = '%s' where nickname = '%s';", oldNickname, newNickname);
        try {
            // todo есть опасность наткнуться на не уникальный ник
            dbConnection.getStmt().executeUpdate(query);
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    @Override
    public boolean isNickBusy(String nickname) {
        String query = String.format("select id from users where nickname = '%s';", nickname);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
        return false;
    }

    @Override
    public void shutdown() {
        dbConnection.close();
    }
}