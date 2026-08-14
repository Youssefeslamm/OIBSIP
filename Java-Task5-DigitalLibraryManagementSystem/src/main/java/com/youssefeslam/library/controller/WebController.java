package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.auth.UserResponse;
import com.youssefeslam.library.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model
    ) {
        UserResponse user = userService.toResponse(
                userService.requireByEmail(
                        authentication.getName()
                )
        );

        model.addAttribute("user", user);
        model.addAttribute(
                "isAdmin",
                user.roles().contains("ROLE_ADMIN")
        );

        return "dashboard";
    }
}