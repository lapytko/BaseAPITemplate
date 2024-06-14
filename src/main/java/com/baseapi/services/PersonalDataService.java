package com.baseapi.services;

import com.baseapi.entity.User.PersonalData;
import com.baseapi.repository.PersonalDataRepository;
import com.baseapi.utils.UpdateObject;
import com.baseapi.utils.encryption.EncryptionHandler;
import com.baseapi.utils.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonalDataService {

    private final EncryptionService encryptionService;
    private final PersonalDataRepository personalDataRepository;
    private final UpdateObject updater;

    @Autowired
    public PersonalDataService(EncryptionService encryptionService, PersonalDataRepository personalDataRepository) {
        this.encryptionService = encryptionService;
        this.personalDataRepository = personalDataRepository;
        this.updater = new UpdateObject();
    }

    public PersonalData createPersonalData(String name, String email, String phoneNumber) throws IllegalAccessException {
        PersonalData personalData = new PersonalData();
        personalData.setName(name);
        personalData.setEmail(email);
        personalData.setPhone(phoneNumber);

        encryptionService.encrypt(personalData);

        return personalData;
    }

    public PersonalData createPersonalData(PersonalData personalData) throws IllegalAccessException {

        encryptionService.encrypt(personalData);

        return personalData;
    }

    public int setDeleteById(String id) {
        return personalDataRepository.setDeleteById(UUID.fromString(id));
    }
}