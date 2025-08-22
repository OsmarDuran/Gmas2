package seguridad;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class PasswordUtil {
    // Ajusta según tu servidor (prueba rendimiento). 120k-200k para SHA-256 suele ser razonable hoy.
    private static final int ITERATIONS = 150_000;
    private static final int KEY_LENGTH_BITS = 256; // 32 bytes
    private static final int SALT_BYTES = 16;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH_BITS);

        // Formato almacenado: pbkdf2$<iter>$<saltB64>$<hashB64>
        return "pbkdf2$" + ITERATIONS + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(char[] password, String stored) {
        try {
            if (stored == null || !stored.startsWith("pbkdf2$")) return false;
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;

            int iters = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] computed = pbkdf2(password, salt, iters, expected.length * 8);
            return constantTimeEquals(expected, computed);
        } catch (Exception ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Error generando PBKDF2", e);
        }
    }

    // Comparación en tiempo constante
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= a[i] ^ b[i];
        return r == 0;
    }
}