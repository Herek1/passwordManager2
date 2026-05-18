package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.ClientHandler;
import server.Logger;
import server.Server;
import server.Util.JsonExtract;

import java.io.IOException;
import java.sql.Connection;

public class RequestHandler {

    private final Connection connection;
    private final Logger logger;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RequestHandler(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public String handle(String request, ClientHandler session) throws IOException {

        JsonNode requestJson = objectMapper.readTree(request);
        String type = requestJson.get("type").asText();

        String response;

        switch (type) {

            case "login":
                response = UserHandler.login(request, connection);

                String idStr = JsonExtract.extract(response, "data", "1", "id");
                Integer userId = idStr != null ? Integer.parseInt(idStr) : null;

                if (userId != null) {
                    session.setUserId(userId);
                }

                System.out.println("User ID extracted: " + userId);
                break;

            case "createUser":
                response = UserHandler.createUser(request, connection);
                break;

            case "registerUser":
                response = UserHandler.setPassword(request, connection);
                break;

            case "getUsers":
                response = UserHandler.getUsers(request, connection);
                break;

            case "deleteUser":
                response = UserHandler.deleteUser(request, connection);
                break;

            case "addPassword":
                response = PasswordHandler.addPassword(request, connection);
                break;

            case "getPassword":
                response = PasswordHandler.getPassword(request, connection);
                break;

            case "deletePassword":
                response = PasswordHandler.deletePassword(request, connection);
                break;

            case "getAuditLogs":
                response = AuditLogHandler.getLogs(request, connection);
                break;

            default:
                response = "{\"success\":false,\"message\":\"Invalid request\"}";
        }
        return response;
    }
}