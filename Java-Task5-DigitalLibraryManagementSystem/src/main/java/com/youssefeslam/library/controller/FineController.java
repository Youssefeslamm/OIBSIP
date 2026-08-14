package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.fine.FineResponse;
import com.youssefeslam.library.service.FineService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping("/me")
    public List<FineResponse> findMyFines(
            Authentication authentication
    ) {
        return fineService.findMyFines(authentication.getName());
    }
}