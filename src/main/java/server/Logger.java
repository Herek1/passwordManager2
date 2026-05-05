package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.AuditLogDAO;
import server.Util.JsonExtract;

public class Logger {

    private final AuditLogDAO auditLogDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Logger(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    public void saveLogs(Integer userId, String ip, String request, String response) {
        try {
            String action = JsonExtract.extract(request, "type");
            String statusStr = JsonExtract.extract(response, "data", "0", "status");
            boolean success = statusStr != null && statusStr.equalsIgnoreCase("success");
            auditLogDAO.addLog(action, userId, ip, success, request, response);
            //Tobefixed
        } catch (Exception e) {
            System.err.println("Error saving logs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}