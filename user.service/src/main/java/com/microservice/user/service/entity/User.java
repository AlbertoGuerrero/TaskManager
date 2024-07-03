package com.microservice.user.service.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="APP_USER")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Username is mandatory")
    @Column(unique = true, nullable = false)
    private String username;
    @NotBlank(message = "Password is mandatory")
    @Column(nullable = false)
    private String password;
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true, nullable = false)
    private String email;
}
