package com.baseapi.controller;

import com.baseapi.controller.base.ApiRequest;
import com.baseapi.controller.base.ApiResponse;
import com.baseapi.controller.base.SaveResponse;
import com.baseapi.dto.user.UserDto;
import com.baseapi.entity.User.User;
import com.baseapi.services.UserService;
import com.baseapi.utils.UpdateObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.modelmapper.ModelMapper;

@RestController
@RequestMapping("/users")
public class UserController {

    ModelMapper modelMapper = new ModelMapper();
    UpdateObject updater = new UpdateObject();

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Object>> saveUser(@RequestBody ApiRequest<UserDto> apiRequest) throws IllegalAccessException {
        ApiResponse<Object> response = new ApiResponse<>(); // Изменено на SaveResponse
        SaveResponse saveResponse = new SaveResponse();

        try {

            UserDto t_user = apiRequest.getData();

            User user = modelMapper.map(t_user, User.class);

            // Обновление существующего пользователя
            if (user.getId() != null) {
                User existingUser = userService.findById(user.getId().toString());
                if (existingUser != null) {
                    User updated = updater.update(user, existingUser);
                    User updatedUser = userService.updateUser(updated);

                    saveResponse.updated(updatedUser.getId().toString());
                    response.success(saveResponse);

                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.notFound().build();
            }

            // Создание нового пользователя
            if (user.getPassword() != null & user.getPassword() != "") {
                User newUser = userService.createUser(user);

                saveResponse.created(newUser.getId().toString());
                response.success(saveResponse);

                return ResponseEntity.ok(response);
            }

            response.error("Password is required");
            return ResponseEntity.internalServerError().body(response);

        } catch (Exception e) {
            response.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }

    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable String id) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            response.success(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            Pageable paging = PageRequest.of(pageNo, pageSize);
            Page<User> pagedResult = userService.findAll(paging);
            if (!pagedResult.hasContent()) {
                response.error("No users found");
                return ResponseEntity.ok(response);
            }

            response.success(pagedResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String id) {
        ApiResponse<Object> response = new ApiResponse<>();
        try{
            User user = userService.findById(id);
            if (user != null) {
                int deleted = userService.deleteById(String.valueOf(user.getId()));
                response.success(deleted);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

