package ru.discode.passwords.helper;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {
    public static byte[]encrypt(String key, byte[] data, String iv) {
        final byte[]keyBytes=md5(key).getBytes();
        final byte[] ivBytes=md5(iv).getBytes();

        try {

            byte[] result;
            SecretKeySpec sks=new SecretKeySpec(keyBytes,"AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(ivBytes));
            result=c.doFinal(data);
            return result;
        }
        catch (Exception e){
            Log.d("encrypt", "Ошибка шифрования! - "+ e);
        }
        return null;
    }
    public static byte[] decrypt (String key, byte[]data, String iv) {
        byte[] result;
        final byte[]keyBytes=md5(key).getBytes();
        final byte[] ivBytes=md5(iv).getBytes();

        try {
            SecretKeySpec sks=new SecretKeySpec(keyBytes, "AES");
            Cipher c=Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE,sks, new IvParameterSpec(ivBytes));
            result=c.doFinal(data);
            return result;
        }
        catch (Exception e) {
            System.out.println("Ошибка дешифровки! - "+e);
        }
        return null;
    }


    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}