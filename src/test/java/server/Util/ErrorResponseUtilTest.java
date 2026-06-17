package server.Util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseUtilTest {

    @Test
    void shouldCreateErrorResponseWithMessage() throws Exception {
        String response = ErrorResponseUtil.createErrorResponse("Test error");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        assertEquals("error", root.get("status").asText());
        assertEquals("Test error", root.get("message").asText());
    }
}