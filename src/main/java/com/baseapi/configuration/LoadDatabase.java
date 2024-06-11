package com.baseapi.configuration;

import com.baseapi.entity.User.Authority;
import com.baseapi.entity.User.PersonalData;
import com.baseapi.entity.User.User;
import com.baseapi.repository.AuthorityRepository;
import com.baseapi.repository.UserRepository;
import com.baseapi.services.PersonalDataService;
import com.baseapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class LoadDatabase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PersonalDataService personalDataService;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            if (authorityRepository.count() == 0) { // only insert if there are no records
                authorityRepository.save(new Authority("ROLE_USER"));
                authorityRepository.save(new Authority("ROLE_ADMIN"));
            }

            if (userRepository.count() == 0) { // only insert if there are no records
                Authority adminAuthority = authorityRepository.findById("ROLE_ADMIN").orElse(null);

                Set<Authority> authorities = new HashSet<>();
                authorities.add(adminAuthority);

                PersonalData personalData = new PersonalData("admin", null, "test@test.com",null);
                User user= new User("admin", "admin", personalData, authorities);
                User registeredUser = userService.createUser(user);

            }
        };
    }
}
