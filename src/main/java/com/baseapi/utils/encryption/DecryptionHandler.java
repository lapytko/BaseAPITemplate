package com.baseapi.utils.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class DecryptionHandler {
    private static DecryptionHandler instance;
    @Autowired
    private Encryptor encryptor;

    private DecryptionHandler(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public static DecryptionHandler getInstance(Encryptor encryptor) {
        if (instance == null) {
            instance = new DecryptionHandler(encryptor);
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
                        Object encrypted = getterMethod.invoke(object);
                        String original = null;
                        if (encrypted instanceof String) {
                            original = encryptor.decrypt((String) encrypted);
                        }
                        if (original != null) {
                            setterMethod.invoke(object, original);
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
