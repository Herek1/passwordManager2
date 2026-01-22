package db.dao;

import db.error.handlers.ErrorHandler;
import db.utils.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public List<HashMap<String, String>> addPassword(String username, String passwordEncrypted, String domain) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> info = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = """
        INSERT INTO passwords (username, password_encrypted, domain)
        VALUES (?, ?, ?)
        ON CONFLICT (username, domain)
        DO UPDATE SET 
            password_encrypted = EXCLUDED.password_encrypted,
            updated_at = NOW()
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordEncrypted);
            stmt.setString(3, domain);
            stmt.executeUpdate();

            info.put("status", "Success");
        } catch (SQLException e) {
            info = errorHandler.handleSQLException(e, info, message);
        }

        result.add(info);
        return result;
    }
}
