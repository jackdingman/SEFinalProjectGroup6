package login.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {
    // Computes the SHA-256 hash of the given input string.
    public static String sha256(String input) {
        try {
            // Creates a SHA-256 message digest instance
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Hash the input bytes (UTF-8)
            byte[] d = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert each byte to a two-digit hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : d) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Wrap and rethrow any exception as a runtime error
            throw new RuntimeException(e);
        }
    }
}
