package com.baseapi.services;

import com.baseapi.entity.User.User;
import com.baseapi.repository.UserRepository;
import com.baseapi.security.AuthenticatedUser;
import com.baseapi.security.JwtService;
import com.baseapi.utils.UpdateObject;
import com.baseapi.utils.encryption.EncryptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final PersonalDataService personalDataService;

    private final UpdateObject updater;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EncryptionService encryptionService, JwtService jwtService, PersonalDataService personalDataService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.personalDataService = personalDataService;
        this.updater = new UpdateObject();
    }

    public User createUser(User user) throws IllegalAccessException {
        encryptionService.encrypt(user.getPersonalData());
        encryptionService.encrypt(user);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) throws IllegalAccessException {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + user.getId() + " не найден"));

        User updated = updater.update(user, existingUser);

        encryptionService.encrypt(updated.getPersonalData());
        encryptionService.encrypt(updated);
        return userRepository.save(updated);
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            try {
                encryptionService.decrypt(user);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
    }

    public User findById(String id) {
        try {
            User user = userRepository.findById(UUID.fromString(id)).orElse(null);
            if (user != null) {
                try {
                    encryptionService.decrypt(user);
                    encryptionService.decrypt(user.getPersonalData());
                } catch (IllegalAccessException e) {
                    return user;
                } finally {
                    return user;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }


    public Authentication getAuthentication(String token) {

        String username = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = findByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Page<User> findAll(Pageable paging) {
        Page<User> users = userRepository.findAll(paging);
        try {
            for (User user : users.getContent()) {
                encryptionService.decrypt(user);
                encryptionService.decrypt(user.getPersonalData());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Transactional
    public int deleteById(String id) throws IllegalAccessException {
        User existingUser = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

        int deleted = userRepository.setDeleteById(UUID.fromString(id));
        if (deleted > 0) {
            if (personalDataService.setDeleteById(String.valueOf(existingUser.getPersonalData().getId())) == 0) {
                throw new RuntimeException("Не удалось удалить персональные данные пользователя");
            }
            encryptionService.encrypt(existingUser.getPersonalData());
            encryptionService.encrypt(existingUser);
        }
        return deleted;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new AuthenticatedUser(user);
    }


    public UserDetailsService getUserDetailsService() {
        return this;
    }
}

