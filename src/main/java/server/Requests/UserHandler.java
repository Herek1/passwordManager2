package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHandler {
    public static String login(String request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String login = root.get("login").asText();
            String password = root.get("password").asText();

            //UsersDAO usersDAO = new UsersDAO(connection);
            List<HashMap<String, String>> dbResponse = new ArrayList<>();

            HashMap<String, String> statusNode = new HashMap<>();
            statusNode.put("status", "Success");
            dbResponse.add(statusNode);

// Element 1: admin
            HashMap<String, String> admin = new HashMap<>();
            admin.put("userType", "user");
            admin.put("login", "1");
            admin.put("name", "Jan");
            admin.put("surname", "Kowalski");
            dbResponse.add(admin);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "login");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return "a";
            //return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }
}