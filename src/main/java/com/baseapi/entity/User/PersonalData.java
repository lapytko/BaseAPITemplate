package com.baseapi.entity.User;

import com.baseapi.entity.BaseEntity;
import com.baseapi.utils.encryption.Encrypted;
import jakarta.persistence.Column;
import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_data")
public class PersonalData extends BaseEntity {
    @Encrypted
    private String name;
    @Encrypted
    private String surname;
    @Encrypted
    @Column(nullable = false)
    private String email;
    @Encrypted
    private String phone;

}
