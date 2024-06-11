package com.baseapi.configuration;

import com.baseapi.utils.encryption.EncryptionHandler;
import com.baseapi.utils.encryption.SimpleEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EncryptionConfig {

    @Autowired
    private Environment env;

    @Bean
    public EncryptionHandler encryptionHandler() {
        return EncryptionHandler.getInstance(SimpleEncryptor.getInstance(env));
    }
}

