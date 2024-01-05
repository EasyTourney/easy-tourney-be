package com.example.easytourneybe.util;

import com.example.easytourneybe.user.UserService;
import com.example.easytourneybe.user.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public class AuthenticationUtil {

    public static User getCurrentUser(UserService userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByEmail(authentication.getName());
    }
}
