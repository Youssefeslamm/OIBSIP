package com.youssefeslam.library.dto.auth;

import java.util.Set;

public record UserResponse(
        Long id,
        String displayName,
        String email,
        boolean enabled,
        Set<String> roles
) {
}