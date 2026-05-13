package server.Util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonExtract {

    public static String extract(String json, String... path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            for (String p : path) {
                if (node == null) return null;

                if (node.isArray()) {
                    try {
                        int index = Integer.parseInt(p);
                        node = node.get(index);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else {
                    node = node.get(p);
                }
            }

            return node != null ? node.asText() : null;

        } catch (Exception e) {
            return null;
        }
    }

    public static int getArraySize(String json, String arrayField) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode arr = root.get(arrayField);
            return (arr != null && arr.isArray()) ? arr.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
