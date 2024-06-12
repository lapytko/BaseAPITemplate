package com.baseapi.services;

import com.baseapi.entity.User.LoginHistory;
import com.baseapi.entity.User.User;
import com.baseapi.repository.LoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryService(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    //generate query to find login history by user id
    public LoginHistory findByUserId(UUID userId) {
        return loginHistoryRepository.findByUserId(userId);
    }

    //save login history for user
    public void save(User user, boolean wasSuccessful) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(user);
        loginHistory.setWasSuccessful(wasSuccessful);
        // установите другие поля loginHistory, если они есть
        loginHistoryRepository.save(loginHistory);
    }
}
