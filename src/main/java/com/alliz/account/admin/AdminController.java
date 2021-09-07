package com.alliz.account.admin;

import com.alliz.account.CurrentAccount;
import com.alliz.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.AccessDeniedException;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin")
    public String adminHomeView(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "admin/index";
    }

    @GetMapping("/create-reservations")
    public String createReservationsView(@CurrentAccount Account account, Model model) throws AccessDeniedException {
        adminService.checkRole(account);
        model.addAttribute(account);
        model.addAttribute(new newReservationForm());
        return "admin/new-reservations";
    }
}
