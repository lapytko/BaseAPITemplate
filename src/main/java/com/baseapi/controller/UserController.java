package com.baseapi.controller;

import com.baseapi.entity.User.Authority;
import com.baseapi.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.baseapi.controller.base.ApiResponse;
import com.baseapi.entity.User.User;
import com.baseapi.services.UserService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<User>> saveUser(@RequestBody User user) throws IllegalAccessException {
        ApiResponse<User> response = new ApiResponse<>();
        if (user.getId() != null) {
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                // Обновление существующего пользователя
                existingUser.setUsername(user.getUsername());
                existingUser.setPassword(user.getPassword());
                Set<Authority> authorities = user.getAuthorities().stream()
                        .map(authority -> new Authority(Role.valueOf(authority.getAuthority()))) // Используем Role.valueOf() для преобразования строки в Role
                        .collect(Collectors.toSet());
                existingUser.setAuthorities(authorities);
                existingUser.setPersonalData(user.getPersonalData());
                response.setData(userService.createUser(existingUser));
                response.setSuccess(true);
                return ResponseEntity.ok(response);
            }
        }
        // Создание нового пользователя
        response.setData(userService.createUser(user));
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable String username) {
        ApiResponse<User> response = new ApiResponse<>();
        User user = userService.findByUsername(username);
        if (user == null) {
            response.setErrorMessage("User not found");
            response.setSuccess(false);
            return ResponseEntity.ok(response);
        }
        response.setData(user);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ApiResponse<Page<User>> response = new ApiResponse<>();
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<User> pagedResult = userService.findAll(paging);
        response.setData(pagedResult);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        ApiResponse<Void> response = new ApiResponse<>();
        User user = userService.findByUsername(username);
        if (user != null) {
            userService.deleteById(String.valueOf(user.getId()));
            response.setSuccess(true);
        } else {
            response.setErrorMessage("User not found");
            response.setSuccess(false);
        }
        return ResponseEntity.ok(response);
    }
}

