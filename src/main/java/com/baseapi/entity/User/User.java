package com.baseapi.entity.User;

import com.baseapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "personal_data_id", referencedColumnName = "id")
    private PersonalData personalData;


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Authority> authorities;


    // Реализация методов интерфейса UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Верните здесь статус аккаунта пользователя
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Верните здесь статус блокировки аккаунта пользователя
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Верните здесь статус срока действия учетных данных пользователя
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Верните здесь статус активации аккаунта пользователя
        return true;
    }

}

