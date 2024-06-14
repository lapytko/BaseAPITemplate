package com.baseapi.dto;

import com.baseapi.entity.User.PersonalData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link PersonalData}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleDto implements Serializable {
    @NotNull
    @Past
    @Future
    @PastOrPresent
    private LocalDateTime created;
    @NotNull
    @Size(min = 2, max = 50)
    @NotEmpty
    @NotBlank
    private String name;
    @NotEmpty
    @NotBlank
    @Length(min = 2)
    private String surname;
    @NotNull
    @Email
    @NotEmpty
    @NotBlank
    private String email;
    @Digits(integer = 12, fraction = 0)
    private String phone;
}