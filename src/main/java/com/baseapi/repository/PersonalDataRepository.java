package com.baseapi.repository;

import com.baseapi.entity.User.PersonalData;
import com.baseapi.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PersonalDataRepository extends JpaRepository<PersonalData, UUID> {
    @Modifying
    @Query("update PersonalData pd set pd.isDeleted = true where pd.id = :id and pd.isDeleted = false")
    int setDeleteById(@Param("id") UUID id);

    @Query("select pd from  PersonalData pd where pd.id = :id and pd.isDeleted = false")
    Optional<PersonalData> findById(@Param("id") UUID id);

    @Query("select pd from  PersonalData pd where pd.id in (select u.personalData.id from User u where u.id = :id and u.isDeleted = false)")
    Optional<PersonalData> findByUserId(@Param("id") UUID id);
}