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
            if (responseData != null && !action.equalsIgnoreCase("getAuditLogs")) {
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

    public List<HashMap<String, String>> searchLogs(AuditLogFilter f) {
        List<HashMap<String, String>> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT
            id,
            timestamp,
            user_id,
            ip_address,
            action,
            success,
            request_data,
            response_data
        FROM audit_logs
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (f.userId != null) {
            sql.append(" AND user_id = ? ");
            params.add(f.userId);
        }

        if (f.action != null) {
            sql.append(" AND action = ? ");
            params.add(f.action);
        }

        if (f.success != null) {
            sql.append(" AND success = ? ");
            params.add(f.success);
        }

        if (f.ipAddress != null) {
            sql.append(" AND ip_address = ? ");
            params.add(f.ipAddress);
        }

        if (f.from != null) {
            sql.append(" AND timestamp >= ? ");
            params.add(java.sql.Timestamp.from(f.from));
        }

        if (f.to != null) {
            sql.append(" AND timestamp <= ? ");
            params.add(java.sql.Timestamp.from(f.to));
        }

        sql.append(" ORDER BY timestamp DESC LIMIT ? OFFSET ? ");
        params.add(f.limit);
        params.add(f.offset);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            var rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, String> row = new HashMap<>();

                row.put("id", String.valueOf(rs.getLong("id")));
                row.put("timestamp", rs.getString("timestamp"));
                row.put("user_id", rs.getString("user_id"));
                row.put("ip_address", rs.getString("ip_address"));
                row.put("action", rs.getString("action"));
                row.put("success", String.valueOf(rs.getBoolean("success")));
                row.put("request_data", rs.getString("request_data"));
                row.put("response_data", rs.getString("response_data"));

                result.add(row);
            }

        } catch (SQLException e) {
            HashMap<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            result.add(err);
        }

        return result;
    }
}