package com.youssefeslam.library.controller;

import com.youssefeslam.library.entity.FineStatus;
import com.youssefeslam.library.entity.MessageStatus;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.service.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final UserService userService;
    private final LoanService loanService;
    private final FineService fineService;
    private final ContactMessageService messageService;

    public AdminWebController(
            UserService userService,
            LoanService loanService,
            FineService fineService,
            ContactMessageService messageService
    ) {
        this.userService = userService;
        this.loanService = loanService;
        this.fineService = fineService;
        this.messageService = messageService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute(
                "users",
                userService.findAll(PageRequest.of(0, 100))
        );

        model.addAttribute(
                "openLoans",
                loanService.findOpenLoans()
        );

        model.addAttribute(
                "unpaidFines",
                fineService.findByStatus(FineStatus.UNPAID)
        );

        model.addAttribute(
                "openMessages",
                messageService.findByStatus(MessageStatus.OPEN)
        );

        return "admin/dashboard";
    }

    @PostMapping("/users/{userId}/disable")
    public String disableUser(
            @PathVariable Long userId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.disable(
                    userId,
                    authentication.getName()
            );

            success(
                    redirectAttributes,
                    "Member account disabled"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException exception
        ) {
            error(redirectAttributes, exception);
        }

        return "redirect:/admin";
    }

    @PostMapping("/users/{userId}/enable")
    public String enableUser(
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.enable(userId);

            success(
                    redirectAttributes,
                    "Member account enabled"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException exception
        ) {
            error(redirectAttributes, exception);
        }

        return "redirect:/admin";
    }

    @PostMapping("/fines/{fineId}/pay")
    public String markFinePaid(
            @PathVariable Long fineId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            fineService.markPaid(fineId);

            success(
                    redirectAttributes,
                    "Fine marked as paid"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException exception
        ) {
            error(redirectAttributes, exception);
        }

        return "redirect:/admin";
    }

    @PostMapping("/messages/{messageId}/resolve")
    public String resolveMessage(
            @PathVariable Long messageId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            messageService.resolve(messageId);

            success(
                    redirectAttributes,
                    "Message marked as resolved"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException exception
        ) {
            error(redirectAttributes, exception);
        }

        return "redirect:/admin";
    }

    private void success(
            RedirectAttributes redirectAttributes,
            String message
    ) {
        redirectAttributes.addFlashAttribute(
                "successMessage",
                message
        );
    }

    private void error(
            RedirectAttributes redirectAttributes,
            RuntimeException exception
    ) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
        );
    }
}