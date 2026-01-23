package server.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.Engine;

import java.io.IOException;
import java.sql.Connection;


public class RequestHandler {
    Connection newConnection;
    Engine myEngine;
    String response = "";
    public RequestHandler() {
        myEngine = new Engine();
        myEngine.start();
        newConnection = myEngine.returnConnection();
    }
    public String handle(String request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode requestJson = objectMapper.readTree(request);

        String type = requestJson.get("type").asText();
        System.out.println("Request type: " + type);
        switch (type){
            case "login":
                response = UserHandler.login(request, newConnection);
                break;
            case "createUser":
                response = UserHandler.createUser(request, newConnection);
                break;
            case "addPassword":
                response = PasswordHandler.addPassword(request, newConnection);
                break;
            case "getPassword":
                response = PasswordHandler.getPassword(request, newConnection);
                break;
            case "deletePassword":
                response = PasswordHandler.deletePassword(request, newConnection);
                break;
            default:
                response = "Invalid request";
                break;
        }
        System.out.println("response: " + response);
        return response;
    }
}