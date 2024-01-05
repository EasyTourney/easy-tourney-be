package com.example.easytourneybe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
