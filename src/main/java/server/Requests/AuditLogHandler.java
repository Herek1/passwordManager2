package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.AuditLogDAO;
import db.dao.AuditLogFilter;
import server.Util.ErrorResponseUtil;

import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class AuditLogHandler {

    public static String getLogs(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            AuditLogFilter f = new AuditLogFilter();

            if (root.has("userId") && !root.get("userId").isNull()) {
                f.userId = root.get("userId").asInt();
            }

            if (root.has("action") && !root.get("action").isNull()) {
                f.action = root.get("action").asText();
            }

            if (root.has("success") && !root.get("success").isNull()) {
                f.success = root.get("success").asBoolean();
            }

            if (root.has("ip") && !root.get("ip").isNull()) {
                f.ipAddress = root.get("ip").asText();
            }

            if (root.has("from") && !root.get("from").isNull()) {
                f.from = Instant.parse(root.get("from").asText());
            }

            if (root.has("to") && !root.get("to").isNull()) {
                f.to = Instant.parse(root.get("to").asText());
            }

            if (root.has("limit") && !root.get("limit").isNull()) {
                f.limit = root.get("limit").asInt();
            }

            if (root.has("offset") && !root.get("offset").isNull()) {
                f.offset = root.get("offset").asInt();
            }

            AuditLogDAO dao = new AuditLogDAO(connection);
            List<HashMap<String, String>> dbResponse = dao.searchLogs(f);

            ObjectNode response = mapper.createObjectNode();
            response.put("type", "getAuditLogs");
            response.set("data", mapper.valueToTree(dbResponse));

            return mapper.writeValueAsString(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse(
                    "Failed to fetch audit logs."
            );
        }
    }
}