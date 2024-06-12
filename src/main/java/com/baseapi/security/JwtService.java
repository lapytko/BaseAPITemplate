package com.baseapi.security;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;

import com.baseapi.entity.User.User;
import com.baseapi.exceptions.AuthorizationException;
import com.baseapi.repository.LoginHistoryRepository;
import com.baseapi.services.LoginHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private final LoginHistoryService loginHistoryService;

    @Autowired
    public JwtService(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;

    }

    public String generateToken(Authentication authentication) {

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        User userPrincipal = authenticatedUser.getUser();

        if (userPrincipal.isLocked()) {
            loginHistoryService.save(userPrincipal, false);
            throw new AuthorizationException("User locked");

        }


        if (!userPrincipal.isEnabled()) {
            loginHistoryService.save(userPrincipal, false);
            throw new AuthorizationException("User is not enabled");
        }

        loginHistoryService.save(userPrincipal, true);

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (InvalidParameterException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}