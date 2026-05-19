package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.AuditLogDAO;
import server.Util.JsonExtract;

public class Logger {

    private final AuditLogDAO auditLogDAO;
    private final ClientHandler session;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Logger(AuditLogDAO auditLogDAO, ClientHandler session) {
        this.auditLogDAO = auditLogDAO;
        this.session = session;
    }

    public void saveLogs(Integer userId, String ip, String request, String response) {
        try {
            String action = JsonExtract.extract(request, "type");
            if(action.equalsIgnoreCase("getAuditLogs")){
                return;
            }
            String statusStr = JsonExtract.extract(response, "data", "0", "status");
            boolean success = statusStr != null && statusStr.equalsIgnoreCase("success");
            System.out.println("result log audit: " + auditLogDAO.addLog(action, userId, ip, success, request, response));
            session.setLastLogResult(success);
            if(success){
                session.setFailCounter(0);
            }else{
                session.setFailCounter(session.getFailCounter()+1);
            }
        } catch (Exception e) {
            System.err.println("Error saving logs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}