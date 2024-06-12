package com.baseapi.security;

import com.baseapi.exceptions.InvalidTokenException;
import com.baseapi.repository.UserRepository;
import com.baseapi.security.JwtService;
import com.baseapi.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    private final JwtService jwtService;
    @Autowired
    @Lazy
    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

        @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURL = request.getRequestURL().toString();
        if (requestURL.contains("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7); // Извлечение токена из заголовка
            if (jwtService.validateToken(authToken)) {
                Authentication auth = getAuthentication(authToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                throw new InvalidTokenException(); // Ваш кастомный exception
            }
        }
        filterChain.doFilter(request, response);
    }

    // Метод для получения аутентификации пользователя
    private Authentication getAuthentication(String token) {
            return userService.getAuthentication(token);

    }
}
