package client;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {
    static String encryptPassword(String masterPassowrd, String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(masterPassowrd.toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] nonce = new byte[12]; // GCM standard nonce size
            new SecureRandom().nextBytes(nonce);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce); // 128-bit auth tag
            cipher.init(Cipher.ENCRYPT_MODE, secret, gcmSpec);

            byte[] ciphertext = cipher.doFinal(password.getBytes());
            byte[] combined = new byte[nonce.length + ciphertext.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(ciphertext, 0, combined, nonce.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String decryptPassword(String masterPassowrd, String encryptedStr, byte[] salt) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedStr);

            byte[] nonce = new byte[12];
            byte[] ciphertext = new byte[combined.length - 12];
            System.arraycopy(combined, 0, nonce, 0, 12);
            System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

            PBEKeySpec spec = new PBEKeySpec(masterPassowrd.toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secret, gcmSpec);

            return new String(cipher.doFinal(ciphertext));
        }catch (AEADBadTagException e) {
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
