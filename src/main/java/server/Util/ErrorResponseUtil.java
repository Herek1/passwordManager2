package server.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
}