package com.baseapi.utils.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class EncryptionHandler {
    private static EncryptionHandler instance;
    @Autowired
    private Encryptor encryptor;

    private EncryptionHandler(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public static EncryptionHandler getInstance(Encryptor encryptor) {
        if (instance == null) {
            instance = new EncryptionHandler(encryptor);
        }
        return instance;
    }

    public void handle(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Encrypted.class)) {
                String fieldName = field.getName();
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    Method getterMethod = object.getClass().getMethod(getterName);
                    Method setterMethod = object.getClass().getMethod(setterName, field.getType());
                    if (getterMethod != null && setterMethod != null) {
                        Object original = getterMethod.invoke(object);
                        String encrypted = null;
                        if (original instanceof String) {
                            encrypted = encryptor.encrypt((String) original);
                        } else if (original instanceof Integer) {
                            encrypted = encryptor.encrypt(Integer.toString((Integer) original));
                        } else if (original instanceof Double) {
                            encrypted = encryptor.encrypt(Double.toString((Double) original));
                        } else if (original instanceof Boolean) {
                            encrypted = encryptor.encrypt(Boolean.toString((Boolean) original));
                        }
                        if (encrypted != null) {
                            setterMethod.invoke(object, encrypted);
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
