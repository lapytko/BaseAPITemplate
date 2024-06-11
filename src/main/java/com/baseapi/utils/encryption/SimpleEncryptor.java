package com.baseapi.utils.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleEncryptor implements Encryptor {

    private static SimpleEncryptor instance;

    @Autowired
    private Environment env;

    private String SECRET_KEY;

    private SimpleEncryptor(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        SECRET_KEY = env.getProperty("data.secret.key");
        System.out.println("Initialized SECRET_KEY: " + SECRET_KEY); // logging
    }

    public static SimpleEncryptor getInstance(Environment env) {
        if (instance == null) {
            instance = new SimpleEncryptor(env);
        }
        return instance;
    }

    @Override
    public String encrypt(String data) {
        try {
            if (data == null) {
                return null;
            }
            Key key = generateKey();
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Key generateKey() {
        byte[] keyBytes = this.SECRET_KEY.getBytes();
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid secret key length. Must be 16, 24, or 32 bytes.");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}
