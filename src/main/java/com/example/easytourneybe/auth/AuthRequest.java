package com.example.easytourneybe.auth;

import jakarta.validation.constraints.Email;
import lombok.*;

import jakarta.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @Email(message = "Invalid email format")
    String email;
    @Pattern(regexp = "^.{6,}$", message = "Password requires at least 6 characters")
    String password;
}
