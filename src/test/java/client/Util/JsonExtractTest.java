package client.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonExtractTest {

    @Test
    void shouldExtractNestedValueFromJsonArray() {
        String json = """
                {
                  "type": "login",
                  "data": [
                    {
                      "status": "Success"
                    },
                    {
                      "username": "user1",
                      "role": "user"
                    }
                  ]
                }
                """;

        String role = JsonExtract.extract(json, "data", "1", "role");

        assertEquals("user", role);
    }

    @Test
    void shouldReturnArraySize() {
        String json = """
                {
                  "data": [
                    {
                      "status": "Success"
                    },
                    {
                      "domain": "example.com"
                    },
                    {
                      "domain": "test.com"
                    }
                  ]
                }
                """;

        int size = JsonExtract.getArraySize(json, "data");

        assertEquals(3, size);
    }
}