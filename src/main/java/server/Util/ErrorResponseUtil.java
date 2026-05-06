package server.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import server.ClientHandler;

public class ErrorResponseUtil {

    public static String createErrorResponse(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("status", "error");
            errorResponse.put("message", message);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Critical failure.\"}";
        }
    }

    public static String createTimeOutResponse(ClientHandler session){
        String timeout = "0";
        switch (session.getFailCounter()){
            case 5:
                timeout = "30";
                break;
            case 10:
                timeout ="60";
                break;
            case 15:
                timeout ="360";
                break;
            case 30:
                timeout ="720";
                break;
            default:
                timeout = "3600";
                break;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("status", "timeout");
            errorResponse.put("message", timeout);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Critical failure.\"}";
        }
    }
}