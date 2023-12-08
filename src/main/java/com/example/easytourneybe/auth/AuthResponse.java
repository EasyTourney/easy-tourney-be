package com.example.easytourneybe.auth;

import com.example.easytourneybe.user.dto.UserDto;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDto userInfo;
}
