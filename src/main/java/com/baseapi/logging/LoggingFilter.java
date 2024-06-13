package com.baseapi.logging;

import com.baseapi.security.JwtService;
import com.baseapi.utils.CachingRequestBodyWrapper;
import com.baseapi.utils.CustomResponseWrapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;


@Order(value = Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = "/*")
@Component
@Slf4j
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
        CachingRequestBodyWrapper wrappedRequest = new CachingRequestBodyWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String requestBody;

        try {
            requestBody = new String(wrappedRequest.getBody());
        }
        catch (Exception e) {
            requestBody = "Request body is empty";
            log.error(e.getMessage());
        }


        filterChain.doFilter(wrappedRequest, wrappedResponse);

        MongoDatabase database = mongoClient.getDatabase("logs");
        MongoCollection<Document> collection;

        String uri = request.getRequestURI();

        if (uri.contains("swagger")) {
            collection = database.getCollection("swagger");
        } else if (uri.contains("v3/api-docs")) {
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
        int status = wrappedResponse.getStatus();  // Получение кода статуса
        String responseBody;

        try {
            responseBody = new String(wrappedResponse.getContentAsByteArray());
            wrappedResponse.copyBodyToResponse();
        }
        catch (Exception e) {
            responseBody = "Response body is empty";
            log.error(e.getMessage());
        }
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
                .append("status", status)
                .append("headers", headers)
                .append("params", params)
                .append("requestBody", requestBody)
                .append("responseBody", responseBody)
                .append("username", username)
                .append("timestamp", new Date());

        collection.insertOne(log);
    }
}