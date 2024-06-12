package com.baseapi.configuration;

import com.baseapi.entity.User.Authority;
import com.baseapi.entity.User.PersonalData;
import com.baseapi.entity.User.User;
import com.baseapi.enums.Role;
import com.baseapi.repository.AuthorityRepository;
import com.baseapi.repository.UserRepository;
import com.baseapi.services.PersonalDataService;
import com.baseapi.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional
    CommandLineRunner initDatabase() {
        return args -> {
            // Получаем список всех существующих ролей
            List<Authority> existingAuthorities = authorityRepository.findAll();

            // Преобразуем список существующих ролей в набор для более быстрого поиска
            Set<String> existingRoles = existingAuthorities.stream()
                    .map(Authority::getAuthority)
                    .collect(Collectors.toSet());

            // перебираем все роли
            for (Role role : Role.values()) {
                // проверяем, существует ли уже такая роль в базе данных
                if (!existingRoles.contains(role.name())) {
                    authorityRepository.save(new Authority(role));
                }
            }

            if (userRepository.count() == 0) { // only insert if there are no records
                Authority adminAuthority = authorityRepository.findByAuthority(Role.ROLE_ADMIN);


                Set<Authority> authorities = new HashSet<>();
                authorities.add(adminAuthority);

                PersonalData personalData = new PersonalData("admin", null, "test@test.com",null);
                // personalData = personalDataService.createPersonalData(personalData); // Сохраните PersonalData сначала

                User user= new User("admin", "admin", personalData, authorities);

                user.setCreated(LocalDateTime.now());
                User registeredUser = userService.createUser(user); // Теперь сохраните User
            }
        };
    }

}
