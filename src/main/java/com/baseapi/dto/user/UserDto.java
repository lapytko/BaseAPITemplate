package com.baseapi.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.baseapi.entity.User.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {
    private UUID id;
    @NotBlank
    private String username;
    private String password;
    @NotNull
    private PersonalDataDto personalData;
    @NotNull
    private Set<AuthorityDto> authorities;
}