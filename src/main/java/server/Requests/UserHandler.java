package server.Requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.dao.UsersDAO;
import server.Util.ErrorResponseUtil;
import server.Util.JsonExtract;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class UserHandler {
    public static String login(String request, Connection connection) {
        try {
            String username = JsonExtract.extract(request, "username");
            String password = JsonExtract.extract(request, "password");

            UsersDAO usersDAO = new UsersDAO(connection);
            List<HashMap<String, String>> dbResponse = usersDAO.getUser(username, password);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "login");
            response.set("data", objectMapper.valueToTree(dbResponse));

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred during login.");
        }
    }
    public static String createUser(String request, Connection connection) {
        UsersDAO usersDAO = new UsersDAO(connection);
        try {
            String username = JsonExtract.extract(request, "username");
            String role = JsonExtract.extract(request, "role");


            List<HashMap<String, String>> dbResponse = usersDAO.createUser(username, role);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("type", "createUser");
            response.set("data", mapper.valueToTree(dbResponse));

            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String deleteUser(String request, Connection connection){

        UsersDAO usersDAO = new UsersDAO(connection);

        try {
            String username = JsonExtract.extract(request, "username");
            PasswordHandler.deleteAllUserPassowrds(request, connection);
            List<HashMap<String, String>> dbResponse = usersDAO.deleteUser(username);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("type", "deleteUser");
            response.set("data", mapper.valueToTree(dbResponse));

            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse("An unexpected error occurred while fetching prescriptions.");
        }
    }

    public static String setPassword(String request, Connection connection) {

        UsersDAO usersDAO = new UsersDAO(connection);

        try {
            String username = JsonExtract.extract(request, "username");
            String password = JsonExtract.extract(request, "password");

            List<HashMap<String, String>> dbResponse = usersDAO.updateUserPassword(username, password);

            ObjectMapper mapper = new ObjectMapper();
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
            String username = JsonExtract.extract(request, "username");
            String role = JsonExtract.extract(request, "role");

            UsersDAO usersDAO = new UsersDAO(connection);

            List<HashMap<String, String>> dbResponse = usersDAO.getUserByUsername(username, role);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();

            response.put("type", "getUsers");
            response.set("data", mapper.valueToTree(dbResponse));

            return mapper.writeValueAsString(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponseUtil.createErrorResponse(
                    "An unexpected error occurred while fetching users."
            );
        }
    }
}