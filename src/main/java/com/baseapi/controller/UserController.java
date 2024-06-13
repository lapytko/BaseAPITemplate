package com.baseapi.controller;

import com.baseapi.Models.Request.User.PersonalDataRequest;
import com.baseapi.Models.Request.User.UserRequest;
import com.baseapi.controller.base.ApiRequest;
import com.baseapi.controller.base.ApiResponse;
import com.baseapi.controller.base.SaveResponse;
import com.baseapi.entity.User.Authority;
import com.baseapi.entity.User.PersonalData;
import com.baseapi.entity.User.User;
import com.baseapi.enums.Role;
import com.baseapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SaveResponse>> saveUser(@RequestBody ApiRequest<UserRequest> apiRequest) throws IllegalAccessException {
        ApiResponse<SaveResponse> response = new ApiResponse<>(); // Изменено на SaveResponse
        SaveResponse saveResponse = new SaveResponse();

        UserRequest t_user = apiRequest.getData();
        User user = new User();
        if (t_user.getId() != null) {
            user.setId(UUID.fromString(t_user.getId()));
        }
        user.setUsername(t_user.getUsername());
        user.setPassword(t_user.getPassword());
        Set<Authority> authorities = t_user.getAuthorities().stream()
                .map(authority -> new Authority(Role.valueOf(authority.getAuthority()))) // Используем Role.valueOf() для преобразования строки в Role
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        PersonalDataRequest t_personalData = t_user.getPersonalData();
        user.setPersonalData(new PersonalData(t_personalData.getName(), t_personalData.getSurname(), t_personalData.getEmail(), t_personalData.getPhone()));

        if (user.getId() != null) {
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                // Обновление существующего пользователя
                existingUser.setUsername(user.getUsername());
                existingUser.setPassword(user.getPassword());
                Set<Authority> authorities_new = user.getAuthorities().stream()
                        .map(authority -> new Authority(Role.valueOf(authority.getAuthority()))) // Используем Role.valueOf() для преобразования строки в Role
                        .collect(Collectors.toSet());
                existingUser.setAuthorities(authorities_new);
                existingUser.setPersonalData(user.getPersonalData());
                User updatedUser = userService.createUser(existingUser);

                saveResponse.setData(updatedUser.getId().toString());
                saveResponse.setNew(false);

                response.setData(saveResponse); // Теперь это работает, потому что ApiResponse ожидает SaveResponse
                response.setSuccess(true);

                return ResponseEntity.ok(response);
            }
        }
        // Создание нового пользователя
        User newUser = userService.createUser(user);

        saveResponse.setData(newUser.getId().toString());
        saveResponse.setNew(false);

        response.setData(saveResponse); // Теперь это работает, потому что ApiResponse ожидает SaveResponse
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable String id) {
        ApiResponse<User> response = new ApiResponse<>();
        User user = userService.findById(id);
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


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        ApiResponse<Void> response = new ApiResponse<>();
        User user = userService.findById(id);
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

