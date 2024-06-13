package com.baseapi.logging;

import com.baseapi.security.JwtService;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.*;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final MongoClient mongoClient;
    private final JwtService jwtService;

    @Autowired
    public LoggingFilter(MongoClient mongoClient, JwtService jwtService) {
        this.mongoClient = mongoClient;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        MongoDatabase database = mongoClient.getDatabase("logs");
        MongoCollection<Document> collection;

        String uri = request.getRequestURI();

        if (uri.contains("swagger")) {
            collection = database.getCollection("swagger");
        }
        else if (uri.contains("v3/api-docs")) {
            collection = database.getCollection("openapi");
        } else {
            collection = database.getCollection("requests");
        }

        String method = request.getMethod();
        Document headers = new Document();
        Document params = new Document();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName, request.getHeader(headerName));
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            params.append(entry.getKey(), String.join(", ", entry.getValue()));
        }

        String requestBody = new String(wrappedRequest.getContentAsByteArray());
        String responseBody = new String(wrappedResponse.getContentAsByteArray());

        String username = "anonymous";
        String authHeader = request.getHeader("Authorization");
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7); // Извлечение токена из заголовка
                if (jwtService.validateToken(authToken)) {
                    username = jwtService.getUsernameFromToken(authToken);
                }
            }
        } catch (Exception e) {
            username = "anonymous";
        }

        Document log = new Document("uri", uri)
                .append("method", method)
                .append("headers", headers)
                .append("params", params)
                .append("requestBody", requestBody)
                .append("responseBody", responseBody)
                .append("username", username)
                .append("timestamp", new Date());

        collection.insertOne(log);

        wrappedResponse.copyBodyToResponse();
    }
}
