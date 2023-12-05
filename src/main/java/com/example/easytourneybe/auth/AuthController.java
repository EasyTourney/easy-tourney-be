package com.example.easytourneybe.auth;

import com.example.easytourneybe.model.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/login")
        public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequest request) {
            return ResponseEntity.ok(ResponseObject.builder().data(authService.authenticate(request)).success(true).build());
    }
}
