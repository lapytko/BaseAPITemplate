package com.baseapi.utils.encryption;

import org.springframework.beans.factory.annotation.Autowired;

public interface Encryptor {
    String encrypt(String data);
    String decrypt(String encryptedData);
}
