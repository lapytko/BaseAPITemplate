package com.baseapi.services;

import com.baseapi.entity.User.PersonalData;
import com.baseapi.utils.encryption.EncryptionHandler;
import com.baseapi.utils.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalDataService {

    private final EncryptionService encryptionService;

    @Autowired
    public PersonalDataService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public PersonalData createPersonalData(String name, String email, String phoneNumber) throws IllegalAccessException {
        PersonalData personalData = new PersonalData();
        personalData.setName(name);
        personalData.setEmail(email);
        personalData.setPhone(phoneNumber);

        encryptionService.encrypt(personalData);

        return personalData;
    }

    public PersonalData createPersonalData(PersonalData data) throws IllegalAccessException {
        PersonalData personalData = new PersonalData();
        personalData.setName(data.getName());
        personalData.setEmail(data.getEmail());
        personalData.setPhone(data.getPhone());

        encryptionService.encrypt(personalData);

        return personalData;
    }
}