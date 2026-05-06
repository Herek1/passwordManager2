package db.dao;

import db.error.handlers.ErrorHandler;
import db.utils.Message;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuditLogDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public AuditLogDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> addLog(String action, Integer userId, String ipAddress, boolean success, String requestData, String responseData) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo1);
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
                PGobject inetObject = new PGobject();
                inetObject.setType("inet");
                inetObject.setValue(ipAddress);
                stmt.setObject(3, inetObject);
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
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
            result.set(0, staticInfo1);
        }
        return result;
    }
}