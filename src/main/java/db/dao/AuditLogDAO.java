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

    public void addLog(String action, Integer userId, String ipAddress, boolean success, String requestData, String responseData, String detailsJson
    ) {
        String query = """
            INSERT INTO audit_logs
            (action, user_id, ip_address, success, request_data, response_data, details)
            VALUES (?, ?, ?, ?, ?, ?, ?::jsonb)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            // action
            stmt.setString(1, action);

            // user_id
            if (userId != null) {
                stmt.setLong(2, userId);
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            // ip_address (INET)
            if (ipAddress != null) {
                stmt.setObject(3, ipAddress);
            } else {
                stmt.setNull(3, Types.OTHER);
            }

            // success
            stmt.setBoolean(4, success);

            // request_data
            if (requestData != null) {
                stmt.setString(5, requestData);
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            // response_data
            if (responseData != null) {
                stmt.setString(6, responseData);
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            // details JSONB
            if (detailsJson != null) {
                stmt.setString(7, detailsJson);
            } else {
                stmt.setNull(7, Types.OTHER);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            // logging must never break main flow
            errorHandler.handleSQLException(
                    e,
                    message.getDefaultErrorMessageAsHashMap(),
                    message
            );
        }
    }
}