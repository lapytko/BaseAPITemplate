package com.baseapi.Models.Request.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDataRequest {
    private String name;
    private String surname;
    private String email;
    private String phone;
}
