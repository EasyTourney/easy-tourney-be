package com.example.easytourneybe.auth;

import com.example.easytourneybe.exceptions.AuthenticationException;
import com.example.easytourneybe.security.jwt.JwtService;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    @Autowired
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception ex) {
            throw new AuthenticationException("Invalid username or password");
        }

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(user);
        UserDto userDto = UserDto.builder().email(user.getEmail()).role(user.getRole()).firstName(user.getFirstName()).lastName(user.getLastName()).build();
        return AuthResponse.builder().token(accessToken).userInfo(userDto).build();
    }
}
