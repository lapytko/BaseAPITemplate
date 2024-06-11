package com.baseapi.logging;


import com.baseapi.security.JwtService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Date;
import java.util.Enumeration;

@Aspect
@Component
public class LoggingAspect {

    private final MongoClient mongoClient;
    private final JwtService jwtService;

    @Autowired
    public LoggingAspect(MongoClient mongoClient, JwtService jwtService) {
        this.mongoClient = mongoClient;
        this.jwtService = jwtService;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        Document headers = new Document();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName, request.getHeader(headerName));
        }

        String token = request.getHeader("Authorization");
        String username = jwtService.getUsernameFromToken(token);
        if (username == null) {
            username = "anonymous";
        }

        ResponseEntity<?> responseEntity = (ResponseEntity<?>) joinPoint.proceed();
        HttpStatusCode responseStatus = responseEntity.getStatusCode();
        Object responseBody = responseEntity.getBody();

        MongoDatabase database = mongoClient.getDatabase("logs");
        MongoCollection<Document> collection = database.getCollection("requests");

        Document log = new Document("uri", uri)
                .append("method", method)
                .append("headers", headers)
                .append("username", username)
                .append("responseStatus", responseStatus.toString())
                .append("responseBody", responseBody.toString())
                .append("timestamp", new Date());

        collection.insertOne(log);

        return responseEntity;
    }
}