package com.baseapi.repository;

import com.baseapi.entity.User.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.username = :username and u.isDeleted = false")
    User findByUsername(@Param("username") String username);

    @Query("select u from User u where u.isDeleted = false")
    Page<User> findAll(Pageable pageable);

    @Modifying
    @Query("update User u set u.isDeleted = true where u.id = :id and u.isDeleted = false")
    int deleteById(@Param("id") String id);

}
