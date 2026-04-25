package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.ClientHandler;
import server.Logger;
import server.Server;

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

                Integer userId = extractUserId(response);
                if (userId != null) {
                    session.setUserId(userId);
                }
                break;

            case "createUser":
                response = UserHandler.createUser(request, connection);
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

            default:
                response = "{\"success\":false,\"message\":\"Invalid request\"}";
        }

        return response;
    }

    private Integer extractUserId(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");

            if (data != null && data.isArray() && !data.isEmpty()) {
                JsonNode user = data.get(0);
                if (user.has("id")) {
                    return user.get("id").asInt();
                }
            }
        } catch (Exception ignored) {}

        return null;
    }
}