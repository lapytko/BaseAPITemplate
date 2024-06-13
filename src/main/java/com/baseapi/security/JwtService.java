package com.baseapi.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baseapi.entity.User.User;
import com.baseapi.exceptions.AuthorizationException;
import com.baseapi.repository.LoginHistoryRepository;
import com.baseapi.services.LoginHistoryService;
import com.baseapi.utils.encryption.StaticEncryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DatatypeConverter;
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

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt_encryption_key}")
    private String jwtEncryptionKey;

    private final LoginHistoryService loginHistoryService;

    @Autowired
    public JwtService(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;

    }

    public String generateToken(Authentication authentication) throws Exception {

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

        Date issueDate = new Date();

        String issuer = generateIssuer(userPrincipal.getUsername(), issueDate);

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuer(issuer)
                .setIssuedAt(issueDate)
                .setExpiration(new Date(issueDate.getTime() + jwtExpirationMs))
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
            Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody();
            String encryptedIssuer = claims.getIssuer();

            String issuerDataJson = new String(StaticEncryptor.decryptAES(Base64.getDecoder().decode(encryptedIssuer), jwtEncryptionKey), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> issuerData = objectMapper.readValue(issuerDataJson, Map.class);

            // Проверьте issuer
            if (!jwtIssuer.equals(issuerData.get("issuer"))) {
                log.error("Invalid JWT issuer");
                return false;
            }

            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
        }

        return false;
    }


    private String generateIssuer(String username, Date issueDate) throws Exception {

        Map<String, Object> issuerData = new HashMap<>();
        issuerData.put("issuer", jwtIssuer);
        issuerData.put("username", username);
        issuerData.put("issuedAt", issueDate);

        ObjectMapper objectMapper = new ObjectMapper();
        String issuerDataJson = objectMapper.writeValueAsString(issuerData);

        String encryptedIssuer = Base64.getEncoder().encodeToString(StaticEncryptor.encryptAES(issuerDataJson.getBytes(StandardCharsets.UTF_8), jwtEncryptionKey));

        return encryptedIssuer;
    }
}