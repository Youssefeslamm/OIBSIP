package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.loan.IssueBookRequest;
import com.youssefeslam.library.dto.loan.LoanResponse;
import com.youssefeslam.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse issueBook(
            Authentication authentication,
            @Valid @RequestBody IssueBookRequest request
    ) {
        return loanService.issueBook(
                authentication.getName(),
                request.bookId()
        );
    }

    @GetMapping("/me")
    public List<LoanResponse> findMyLoans(
            Authentication authentication
    ) {
        return loanService.findMyLoans(authentication.getName());
    }

    @PostMapping("/{loanId}/return")
    public LoanResponse returnBook(
            Authentication authentication,
            @PathVariable Long loanId
    ) {
        return loanService.returnBook(
                authentication.getName(),
                loanId
        );
    }
}