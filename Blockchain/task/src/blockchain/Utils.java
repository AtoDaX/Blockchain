package blockchain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

public class Utils {
    // Serialize object.
    public static String serializeObject(Serializable object) {
        String results;

        try (var byteArrayOutputStream = new ByteArrayOutputStream(); var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            results = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error serializing object.");
        }

        return results;
    }

    // Deserialize object.
    public static <T extends Serializable> T deserializeObject(String objectAsString) {
        T results;

        byte[] data = Base64.getDecoder().decode(objectAsString);
        try (var byteArrayInputStream = new ByteArrayInputStream(data); var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            results = (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Unexpected error deserializing object.");
        }

        return results;
    }

    // Hash string.
    public static String hashString(String input) {
        String results;

        try {
            var messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            results = HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unexpected error getting message digest algorithm.");
        }

        return results;
    }
}