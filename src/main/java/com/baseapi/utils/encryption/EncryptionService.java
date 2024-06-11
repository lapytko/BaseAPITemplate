package com.baseapi.utils.encryption;

import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private final EncryptionHandler encryptionHandler;
    private final DecryptionHandler decryptionHandler;

    public EncryptionService(EncryptionHandler encryptionHandler, DecryptionHandler decryptionHandler) {
        this.encryptionHandler = encryptionHandler;
        this.decryptionHandler = decryptionHandler;
    }

    public void encrypt(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        encryptionHandler.handle(obj);
    }

    public void decrypt(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        decryptionHandler.handle(obj);
    }
}

