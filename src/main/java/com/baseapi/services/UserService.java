package com.baseapi.services;

import com.baseapi.entity.User.Authority;
import com.baseapi.entity.User.PersonalData;
import com.baseapi.entity.User.User;
import com.baseapi.repository.UserRepository;
import com.baseapi.security.AuthenticatedUser;
import com.baseapi.utils.encryption.EncryptionHandler;
import com.baseapi.utils.encryption.EncryptionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
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

    public User createUser(String username, String password) throws IllegalAccessException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        encryptionService.encrypt(user);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
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

