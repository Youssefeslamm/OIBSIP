package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.message.ContactMessageResponse;
import com.youssefeslam.library.entity.MessageStatus;
import com.youssefeslam.library.service.ContactMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/messages")
public class AdminContactMessageController {

    private final ContactMessageService messageService;

    public AdminContactMessageController(
            ContactMessageService messageService
    ) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<ContactMessageResponse> findByStatus(
            @RequestParam(defaultValue = "OPEN")
            MessageStatus status
    ) {
        return messageService.findByStatus(status);
    }

    @PatchMapping("/{messageId}/resolve")
    public ContactMessageResponse resolve(
            @PathVariable Long messageId
    ) {
        return messageService.resolve(messageId);
    }
}