package mathapp.gui;

/**
 * Minimal JSON extraction without adding dependencies.
 * Extracts the value for keys like "token", "username", "email", "error", "message".
 */
public class JacksonLite {

    public static String extractString(String json, String key) {
        if (json == null || key == null || key.isBlank()) {
            return null;
        }

        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);

        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        int valueStart = json.indexOf('"', colonIndex) + 1;
        if (valueStart == 0) {
            return null;
        }

        int valueEnd = json.indexOf('"', valueStart);
        if (valueEnd == -1) {
            return null;
        }

        return json.substring(valueStart, valueEnd);
    }
    
    public static String extractNestedString(String json, String wrapperKey, String nestedKey) {
        if (json == null || wrapperKey == null || wrapperKey.isBlank()) return null;
        String pattern = "\"" + wrapperKey + "\"";
        int idx = json.indexOf(pattern);

        if (idx < 0) return null;
        return extractString(json.substring(idx), nestedKey);
    }
}


