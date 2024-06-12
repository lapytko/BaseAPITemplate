package com.baseapi.repository;

import com.baseapi.entity.User.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {

    //generate query to find login history by user id
    public LoginHistory findByUserId(UUID userId);


}