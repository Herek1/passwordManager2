package client.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionTest {

    @Test
    void shouldEncryptAndDecryptPassword() throws Exception {
        String masterPassword = "Master123!";
        String password = "SecretPassword123";

        String encrypted = Encryption.encryptPassword(masterPassword, password);
        String decrypted = Encryption.decryptPassword(masterPassword, encrypted);

        assertNotEquals(password, encrypted);
        assertEquals(password, decrypted);
    }

    @Test
    void shouldHashPasswordDeterministically() throws Exception {
        String password = "Master123!";
        String password2 = "Master1234!";

        String hash1 = Encryption.hashPassword(password);
        String hash2 = Encryption.hashPassword(password);
        String hash3 = Encryption.hashPassword(password2);

        assertEquals(hash1, hash2);
        assertNotEquals(password, hash1);
        assertNotEquals(hash1, hash3);
    }
}