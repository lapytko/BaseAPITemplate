package com.baseapi.repository;


import com.baseapi.entity.User.Authority;
import com.baseapi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Role> {

    public Authority findByAuthority(Role authority);

    public void deleteByAuthority(Role authority);

    public List<Authority> findAll();
}
