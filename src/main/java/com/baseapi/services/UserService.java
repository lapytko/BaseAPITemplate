package com.baseapi.services;

import com.baseapi.entity.User.Authority;
import com.baseapi.entity.User.User;
import com.baseapi.repository.UserRepository;
import com.baseapi.security.AuthenticatedUser;
import com.baseapi.security.JwtService;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EncryptionService encryptionService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public User createUser(User usr) throws IllegalAccessException {
        User user = new User();
        user.setUsername(usr.getUsername());
        user.setPassword(passwordEncoder.encode(usr.getPassword()));
        Set<Authority> authorities = new HashSet<>(usr.getAuthorities().stream()
                .map(authority -> (Authority) authority)
                .collect(Collectors.toSet()));
        user.setAuthorities(authorities);

        user.setPersonalData(usr.getPersonalData());


        encryptionService.encrypt(user.getPersonalData());
        encryptionService.encrypt(user);
        return userRepository.save(user);
    }
    @Transactional
    public User updateUser(User usr) throws IllegalAccessException {
        User existingUser = userRepository.findById(usr.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + usr.getId() + " не найден"));

        existingUser.setUsername(usr.getUsername());
        existingUser.setPassword(passwordEncoder.encode(usr.getPassword()));
        Set<Authority> authorities = new HashSet<>(usr.getAuthorities().stream()
                .map(authority -> (Authority) authority)
                .collect(Collectors.toSet()));
        existingUser.setAuthorities(authorities);
        existingUser.setPersonalData(usr.getPersonalData());

        encryptionService.encrypt(existingUser.getPersonalData());
        encryptionService.encrypt(existingUser);
        return userRepository.save(existingUser);
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
        User user = userRepository.findById(UUID.fromString(id)).orElse(null);
        if (user != null) {
            try {
                encryptionService.decrypt(user);
                encryptionService.decrypt(user.getPersonalData());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
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
    public int deleteById(String id) {
        return userRepository.setDeleteById(UUID.fromString(id));
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

