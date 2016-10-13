package ru.discode.passwords.helper;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {/**
 * Encrypts the text.
 * @param clearText The text you want to encrypt
 * @return Encrypted data if successful, or null if unsucessful
 */
public static String encrypt(String clearText, String seed) {
    byte[] encryptedText = null;
    try {
        byte[] keyData = seed.getBytes();
        SecretKey ks = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, ks);
        encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedText, Base64.DEFAULT);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

    /**
     * Decrypts the text
     * @param encryptedText The text you want to encrypt
     * @return Decrypted data if successful, or null if unsucessful
     */
    public static String decrypt (String encryptedText, String seed) {
        byte[] clearText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}