package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.message.ContactMessageRequest;
import com.youssefeslam.library.dto.message.ContactMessageResponse;
import com.youssefeslam.library.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class ContactMessageController {

    private final ContactMessageService messageService;

    public ContactMessageController(
            ContactMessageService messageService
    ) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactMessageResponse create(
            Authentication authentication,
            @Valid @RequestBody ContactMessageRequest request
    ) {
        return messageService.create(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/me")
    public List<ContactMessageResponse> findMine(
            Authentication authentication
    ) {
        return messageService.findMine(
                authentication.getName()
        );
    }
}