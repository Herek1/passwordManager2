package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.PasswordsDAO;
import db.dao.UsersDAO;
import server.Util.ErrorResponseUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHandler {
    public static String login(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();
            String password = root.get("password").asText();

            UsersDAO usersDAO = new UsersDAO(connection);
            List<HashMap<String, String>> dbResponse = usersDAO.getUser(username, password);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "login");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }
    public static String createUser(String request, Connection connection) {
        UsersDAO usersDAO = new UsersDAO(connection);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(request);
            String username = root.get("username").asText();
            String role = root.get("role").asText();


            List<HashMap<String, String>> dbResponse = usersDAO.createUser(username, role);

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "createUser");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));
            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String setPassword(String request, Connection connection) {

        UsersDAO usersDAO = new UsersDAO(connection);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);
            String username = root.get("username").asText();
            String password = root.get("password").asText();

            List<HashMap<String, String>> dbResponse = usersDAO.updateUserPassword(username, password);
            ObjectNode response = mapper.createObjectNode();

            response.put("type", "setPassword");
            response.set("data", mapper.valueToTree(dbResponse));

            return mapper.writeValueAsString(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("Failed to set password.");
        }
    }

    public static String getUsers(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = null;
            if (root.has("username") && !root.get("username").isNull()) {
                username = root.get("username").asText();
            }
            String role = null;
            if (root.has("role") && !root.get("role").isNull()) {
                role = root.get("role").asText();
            }

            UsersDAO usersDAO = new UsersDAO(connection);
            List<HashMap<String, String>> dbResponse = usersDAO.getUserByUsername(username, role);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "getUsers");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse(
                    "An unexpected error occurred while fetching users."
            );
        }
    }
}