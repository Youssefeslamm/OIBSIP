package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.loan.LoanResponse;
import com.youssefeslam.library.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
public class AdminLoanController {

    private final LoanService loanService;

    public AdminLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanResponse> findOpenLoans() {
        return loanService.findOpenLoans();
    }
}