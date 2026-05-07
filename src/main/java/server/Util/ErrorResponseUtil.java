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
        String timeout = switch (session.getFailCounter()) {
            case 5 -> "30";
            case 10 -> "60";
            case 15 -> "360";
            case 30 -> "720";
            default -> "3600";
        };
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("type", "timeout");
            errorResponse.put("message", timeout);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Critical failure.\"}";
        }
    }
}