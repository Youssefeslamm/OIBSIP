package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.auth.RegisterRequest;
import com.youssefeslam.library.dto.auth.UserResponse;
import com.youssefeslam.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return userService.register(request);
    }

    @GetMapping("/me")
    public UserResponse currentUser(Authentication authentication) {
        return userService.toResponse(
                userService.requireByEmail(authentication.getName())
        );
    }
}