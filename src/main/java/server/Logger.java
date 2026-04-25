package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.AuditLogDAO;

public class Logger {

    private final AuditLogDAO auditLogDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Logger(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    public void saveLogs(
            Integer userId,
            String ip,
            String request,
            String response
    ) {
        try {
            JsonNode req = parse(request);
            JsonNode res = parse(response);

            String action = extract(req, "type", "UNKNOWN");

            boolean success = extractSuccess(res);

            String details = buildDetails(req, res);

            auditLogDAO.addLog(
                    action,
                    userId,
                    ip,
                    success,
                    request,
                    response,
                    details
            );

        } catch (Exception ignored) {
            // logging must never break flow
        }
    }

    private JsonNode parse(String json) {
        try {
            if (json == null || json.isBlank()) return null;
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String extract(JsonNode node, String field, String fallback) {
        if (node == null || !node.has(field)) return fallback;
        return node.get(field).asText(fallback);
    }

    private boolean extractSuccess(JsonNode res) {
        if (res == null) return false;
        return res.has("success") && res.get("success").asBoolean(false);
    }

    private String buildDetails(JsonNode req, JsonNode res) {
        try {
            ObjectNode root = objectMapper.createObjectNode();

            root.set("request", req != null ? req : objectMapper.createObjectNode());
            root.set("response", res != null ? res : objectMapper.createObjectNode());

            return objectMapper.writeValueAsString(root);

        } catch (Exception e) {
            return "{}";
        }
    }
}