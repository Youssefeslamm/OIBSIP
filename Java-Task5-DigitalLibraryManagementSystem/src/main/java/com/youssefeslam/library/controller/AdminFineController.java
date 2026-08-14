package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.fine.FineResponse;
import com.youssefeslam.library.entity.FineStatus;
import com.youssefeslam.library.service.FineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fines")
public class AdminFineController {

    private final FineService fineService;

    public AdminFineController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping
    public List<FineResponse> findByStatus(
            @RequestParam(defaultValue = "UNPAID")
            FineStatus status
    ) {
        return fineService.findByStatus(status);
    }

    @PatchMapping("/{fineId}/pay")
    public FineResponse markPaid(@PathVariable Long fineId) {
        return fineService.markPaid(fineId);
    }
}