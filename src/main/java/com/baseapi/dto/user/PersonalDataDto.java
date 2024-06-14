package com.baseapi.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.baseapi.entity.User.PersonalData}
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalDataDto implements Serializable {
    @NotNull
    @NotEmpty
    private String name;
    private String surname;
    @Email
    @NotBlank
    private String email;
    private String phone;
}