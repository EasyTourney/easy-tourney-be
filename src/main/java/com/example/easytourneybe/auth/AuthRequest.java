package com.example.easytourneybe.auth;

import jakarta.validation.constraints.Email;
import lombok.*;

import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @Email(message = "Invalid email format")
    @Length(max = 50)
    String email;
    @Pattern(regexp = "^.{6,}$", message = "Password requires at least 6 characters")
    String password;

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
