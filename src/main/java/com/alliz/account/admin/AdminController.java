package com.alliz.account.admin;

import com.alliz.account.CurrentAccount;
import com.alliz.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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

    @GetMapping("/admin/create-reservations")
    public String createReservationsView(@CurrentAccount Account account, Model model) throws AccessDeniedException {
        adminService.checkRole(account);
        model.addAttribute(account);
        model.addAttribute(new ReservationForm());
        return "admin/new-reservations";
    }

    @PostMapping("/admin/reservations/add")
    public String addReservation(@CurrentAccount Account account, @Valid ReservationForm form, Errors errors, Model model, RedirectAttributes attributes) throws AccessDeniedException {
        adminService.checkRole(account);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "admin/new-reservations";
        }
        if (adminService.isAlreadyReservation(form.getReservationTime())) {
            model.addAttribute("error", "이 시간의 배차는 이미 등록되었습니다. 배차조회를 확인하세요.");
            model.addAttribute(account);
            return "admin/new-reservations";
        }

        adminService.addNewReservation(form);

        String message = "[장소: " + form.getReservationLocation() + ", 시간: " + form.getReservationTime() + "] 차량 시간을 등록했습니다.";
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/create-reservations";
    }
}
