package main;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256_CryptClass {

    private static String salt = "This is the salt";

    public static String encrypt(String strToEncrypt, String Key) throws Exception {
        String paddedKey = String.format("%16s", Key).replace(' ', '0');
        IvParameterSpec ivspec = new IvParameterSpec(salt.getBytes("UTF-8"));
        SecretKeySpec secretKey = new SecretKeySpec(paddedKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }

    public static String decrypt(String strToDecrypt, String Key) throws Exception {
        String paddedKey = String.format("%16s", Key).replace(' ', '0');
        IvParameterSpec ivspec = new IvParameterSpec(salt.getBytes("UTF-8"));
        SecretKeySpec secretKey = new SecretKeySpec(paddedKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
}
