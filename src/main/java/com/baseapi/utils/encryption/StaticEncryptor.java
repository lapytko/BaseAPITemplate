package com.baseapi.utils.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class StaticEncryptor {

    private static final String AES = "AES";
    private static final String SHA_256 = "SHA-256";

    public static byte[] encryptAES(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptAES(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] getSHA256Hash(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance(SHA_256);
        return md.digest(data);
    }
}

