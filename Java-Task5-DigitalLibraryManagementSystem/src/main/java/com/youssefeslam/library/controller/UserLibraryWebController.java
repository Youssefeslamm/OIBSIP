package com.youssefeslam.library.controller;

import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.service.LoanService;
import com.youssefeslam.library.service.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserLibraryWebController {

    private final LoanService loanService;
    private final ReservationService reservationService;

    public UserLibraryWebController(
            LoanService loanService,
            ReservationService reservationService
    ) {
        this.loanService = loanService;
        this.reservationService = reservationService;
    }

    @PostMapping("/books/{bookId}/issue")
    public String issueBook(
            @PathVariable Long bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            loanService.issueBook(
                    authentication.getName(),
                    bookId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book issued successfully"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/books";
    }

    @PostMapping("/books/{bookId}/reserve")
    public String reserveBook(
            @PathVariable Long bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reservationService.create(
                    authentication.getName(),
                    bookId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Reservation added to the waiting queue"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/books";
    }

    @GetMapping("/my-loans")
    public String myLoans(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "loans",
                loanService.findMyLoans(
                        authentication.getName()
                )
        );

        return "loans/my-loans";
    }

    @PostMapping("/my-loans/{loanId}/return")
    public String returnBook(
            @PathVariable Long loanId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            loanService.returnBook(
                    authentication.getName(),
                    loanId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book returned successfully"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/my-loans";
    }

    @GetMapping("/my-reservations")
    public String myReservations(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "reservations",
                reservationService.findMine(
                        authentication.getName()
                )
        );

        return "reservations/my-reservations";
    }

    @PostMapping(
            "/my-reservations/{reservationId}/cancel"
    )
    public String cancelReservation(
            @PathVariable Long reservationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reservationService.cancel(
                    authentication.getName(),
                    reservationId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Reservation cancelled"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/my-reservations";
    }
}