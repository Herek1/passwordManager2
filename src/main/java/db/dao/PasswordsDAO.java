package db.dao;

import db.error.handlers.ErrorHandler;
import db.utils.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PasswordsDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public PasswordsDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> addPassword(String username, String login, String passwordEncrypted, String domain) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> info = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = """
        INSERT INTO passwords (username, login, password_encrypted, domain)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (username, domain, login)
        DO UPDATE SET 
            password_encrypted = EXCLUDED.password_encrypted,
            updated_at = NOW()
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, login);
            stmt.setString(3, passwordEncrypted);
            stmt.setString(4, domain);
            stmt.executeUpdate();

            info.put("status", "Success");
        } catch (SQLException e) {
            info = errorHandler.handleSQLException(e, info, message);
        }

        result.add(info);
        return result;
    }

    public List<HashMap<String, String>> deletePassword(String username, String login, String domain) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> info = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = """
        DELETE FROM passwords
        WHERE username = ?
          AND login = ?
          AND domain = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, login);
            stmt.setString(3, domain);

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                info.put("status", "NotFound");
            } else {
                info.put("status", "Success");
            }
        } catch (SQLException e) {
            info = errorHandler.handleSQLException(e, info, message);
        }

        result.add(info);
        return result;
    }

    public List<HashMap<String, String>> getPassword(String username, String url){
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo =
                new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo);

        try {
            String query = """
            SELECT login, domain, password_encrypted
            FROM passwords
            WHERE username = ?
              AND domain ILIKE ?
            ORDER BY domain
        """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                if (url == null || url.isBlank()) {
                    stmt.setString(2, "%");
                } else {
                    stmt.setString(2, "%" + url + "%");
                }
                System.out.println("DB TEST" + url);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> row = new HashMap<>();
                        row.put("domain", rs.getString("domain"));
                        row.put("login", rs.getString("login"));
                        row.put("password", rs.getString("password_encrypted"));
                        result.add(row);
                    }
                }

                if (result.size() == 1) {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(
                            message.getHashIdUserFriendlyError(),
                            "No passwords found"
                    );
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "success");
                }

            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.set(0, staticInfo);
        }

        return result;
    }
}
