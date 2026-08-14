package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.message.ContactMessageRequest;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.service.ContactMessageService;
import com.youssefeslam.library.service.FineService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAccountWebController {

    private final FineService fineService;
    private final ContactMessageService messageService;

    public UserAccountWebController(
            FineService fineService,
            ContactMessageService messageService
    ) {
        this.fineService = fineService;
        this.messageService = messageService;
    }

    @GetMapping("/my-fines")
    public String myFines(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "fines",
                fineService.findMyFines(
                        authentication.getName()
                )
        );

        return "fines/my-fines";
    }

    @GetMapping("/contact")
    public String contact(
            Authentication authentication,
            Model model
    ) {
        if (!model.containsAttribute("messageRequest")) {
            model.addAttribute(
                    "messageRequest",
                    new ContactMessageRequest("", "")
            );
        }

        model.addAttribute(
                "messages",
                messageService.findMine(
                        authentication.getName()
                )
        );

        return "messages/contact";
    }

    @PostMapping("/contact")
    public String submitContactMessage(
            Authentication authentication,
            @Valid @ModelAttribute("messageRequest")
            ContactMessageRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "messages",
                    messageService.findMine(
                            authentication.getName()
                    )
            );

            return "messages/contact";
        }

        try {
            messageService.create(
                    authentication.getName(),
                    request
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your message has been submitted"
            );
        } catch (
                BusinessRuleException
                | IllegalArgumentException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/contact";
    }
}