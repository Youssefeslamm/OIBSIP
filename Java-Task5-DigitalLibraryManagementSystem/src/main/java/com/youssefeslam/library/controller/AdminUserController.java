package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.auth.UserResponse;
import com.youssefeslam.library.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponse> findAll(
            @PageableDefault(
                    size = 20,
                    sort = "displayName"
            )
            Pageable pageable
    ) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{userId}")
    public UserResponse findById(
            @PathVariable Long userId
    ) {
        return userService.findById(userId);
    }

    @PatchMapping("/{userId}/disable")
    public UserResponse disable(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return userService.disable(
                userId,
                authentication.getName()
        );
    }

    @PatchMapping("/{userId}/enable")
    public UserResponse enable(
            @PathVariable Long userId
    ) {
        return userService.enable(userId);
    }
}