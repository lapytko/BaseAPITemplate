package com.baseapi.entity.User;

import com.baseapi.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authorities")
public class Authority implements GrantedAuthority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_name", nullable = false)
    private Role authority;

    @Override
    public String getAuthority() {
        return authority.name();
    }
}
