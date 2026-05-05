package db.dao;

import db.error.handlers.ErrorHandler;
import db.utils.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AuditLogDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public AuditLogDAO(Connection conn) {
        this.conn = conn;
    }

    public void addLog(String action, Integer userId, String ipAddress, boolean success, String requestData, String responseData) {
        String query = """
            INSERT INTO audit_logs
            (action, user_id, ip_address, success, request_data, response_data)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, action);

            if (userId != null) {
                stmt.setLong(2, userId);
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            if (ipAddress != null) {
                stmt.setObject(3, ipAddress);
            } else {
                stmt.setNull(3, Types.OTHER);
            }

            stmt.setBoolean(4, success);

            if (requestData != null) {
                stmt.setString(5, requestData);
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            if (responseData != null) {
                stmt.setString(6, responseData);
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            errorHandler.handleSQLException(
                    e,
                    message.getDefaultErrorMessageAsHashMap(),
                    message
            );
        }
    }
}