package server.Requests;

import client.Util.Encryption;
import client.Util.UserSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.PasswordsDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import server.Util.ErrorResponseUtil;
import server.Util.JsonExtract;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class PasswordHandler {
    public static String addPassword(String request, Connection connection){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();
            String login = root.get("login").asText();
            String password = root.get("password").asText();
            String domain = root.get("domain").asText();

            PasswordsDAO passwordsDAO = new PasswordsDAO(connection);
            List<HashMap<String, String>> dbResponse = passwordsDAO.addPassword(username,login, password,domain);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "addPassword");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }

    public static String deletePassword(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();
            String login = root.get("login").asText();
            String domain = root.get("domain").asText();

            PasswordsDAO passwordsDAO = new PasswordsDAO(connection);
            List<HashMap<String, String>> dbResponse = passwordsDAO.deletePassword(username, login, domain);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "deletePassword");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse(
                    "An unexpected error occurred while deleting password."
            );
        }
    }


    public static String getPassword(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();
            String url = null;
            if (root.has("url") && !root.get("url").isNull()) {
                url = root.get("url").asText();
            }

            PasswordsDAO passwordsDAO = new PasswordsDAO(connection);
            List<HashMap<String, String>> dbResponse = passwordsDAO.getPassword(username, url);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "getPasswords");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching passwords.");
        }
    }

    public static String deleteAllUserPassowrds(String request, Connection connection) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();

            PasswordsDAO passwordsDAO = new PasswordsDAO(connection);
            List<HashMap<String, String>> dbResponse = passwordsDAO.deleteAllPasswords(username);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonResponseNode = objectMapper.createObjectNode();
            jsonResponseNode.put("type", "deletePassword");
            jsonResponseNode.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(jsonResponseNode);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse(
                    "An unexpected error occurred while deleting password."
            );
        }
    }
}
