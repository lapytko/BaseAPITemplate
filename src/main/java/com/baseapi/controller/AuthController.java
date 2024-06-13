package com.baseapi.controller;

import com.baseapi.Models.Request.LoginRequest;
import com.baseapi.Models.Response.JwtAuthenticationResponse;
import com.baseapi.controller.base.ApiRequest;
import com.baseapi.controller.base.ApiResponse;
import com.baseapi.entity.User.User;
import com.baseapi.exceptions.DuplicateUsernameException;
import com.baseapi.security.JwtService;
import com.baseapi.services.LoginHistoryService;
import com.baseapi.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LoginHistoryService loginHistoryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody ApiRequest<LoginRequest> loginRequest) {
        String jwt;

        try {
            LoginRequest request = loginRequest.getData();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    ));
            jwt = jwtService.generateToken(authentication);

        log.info("User {} logged in", request.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(new JwtAuthenticationResponse(jwt), null, true));
        } catch (Exception e) {
            User user = userService.findByUsername(loginRequest.getData().getUsername());
            if (user != null) {
                loginHistoryService.save(user, false);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }

    }
}

