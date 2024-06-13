package com.baseapi.Models.Request.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String id;
    private String username;
    private String password;
    private PersonalDataRequest personalData;
    private List<AuthorityRequest> authorities;
}

