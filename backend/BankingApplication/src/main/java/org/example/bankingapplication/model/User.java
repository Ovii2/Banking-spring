package org.example.bankingapplication.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.bankingapplication.enums.Roles;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private Roles role;

}
