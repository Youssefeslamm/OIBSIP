package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.auth.RegisterRequest;
import com.youssefeslam.library.dto.auth.UserResponse;
import com.youssefeslam.library.entity.Role;
import com.youssefeslam.library.entity.RoleName;
import com.youssefeslam.library.entity.User;
import com.youssefeslam.library.exception.DuplicateResourceException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.RoleRepository;
import com.youssefeslam.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists"
            );
        }

        Role userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ROLE_USER has not been configured"
                ));

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.displayName().trim()
        );

        user.addRole(userRole);

        return toResponse(userRepository.save(user));
    }

    public User requireByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
    }
}