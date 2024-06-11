package com.baseapi.repository;

import com.baseapi.entity.User.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonalDataRepository extends JpaRepository<PersonalData, UUID> {
}