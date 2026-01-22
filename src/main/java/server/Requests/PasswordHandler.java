package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.PasswordsDAO;
import db.dao.UsersDAO;
import server.Util.ErrorResponseUtil;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class PasswordHandler {
    public static String addPassword(String request, Connection connection){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request);

            String username = root.get("username").asText();
            String password = root.get("password").asText();
            String domain = root.get("domain").asText();

            PasswordsDAO passwordsDAO = new PasswordsDAO(connection);
            List<HashMap<String, String>> dbResponse = passwordsDAO.addPassword(username, password,domain);

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
}
