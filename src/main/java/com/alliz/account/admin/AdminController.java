package com.alliz.account.admin;

import com.alliz.account.CurrentAccount;
import com.alliz.domain.Account;
import com.alliz.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.Message;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ModelMapper modelMapper;

    @GetMapping("/admin")
    public String adminHomeView(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "admin/index";
    }

    @GetMapping("/admin/new-reservations")
    public String createReservationsView(@CurrentAccount Account account, Model model) {
        adminService.checkRole(account);
        model.addAttribute(account);
        model.addAttribute(new ReservationForm());
        return "admin/new-reservations";
    }

    @PostMapping("/admin/reservation/add")
    public String uploadReservation(@CurrentAccount Account account, @Valid ReservationForm form, Errors errors, Model model, RedirectAttributes attributes) {
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
        return "redirect:/admin/new-reservations";
    }

    @GetMapping("/admin/reservation/remove/{id}")
    public String removeReservation(@CurrentAccount Account account, @PathVariable Long id) {
        adminService.checkRole(account);
        adminService.removeReservationById(id);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/admin/reservation/edit/{id}")
    public String editReservation(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        adminService.checkRole(account);
        Reservation reservation = adminService.findReservationById(id);
        model.addAttribute(modelMapper.map(reservation, ReservationForm.class));
        model.addAttribute(account);
        model.addAttribute("reservationId", id);
        return "admin/edit-reservations";
    }

    @PostMapping("/admin/reservation/edit/{id}")
    public String editReservation(@CurrentAccount Account account,@PathVariable Long id,
                                  @Valid ReservationForm form, Errors errors, Model model, RedirectAttributes attributes) {
        adminService.checkRole(account);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "admin/edit-reservations";
        }
        if (adminService.isAlreadyReservation(form.getReservationTime())) {
            model.addAttribute("error", "이 시간의 배차는 이미 등록되었습니다. 배차조회를 확인하세요.");
            model.addAttribute(account);
            return "admin/edit-reservations";
        }
        adminService.editReservation(id, form);
        model.addAttribute(account);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/admin/reservations")
    public String reservationsView(@CurrentAccount Account account, Model model) {
        adminService.checkRole(account);
        List<Reservation> reservationList = adminService.getAllReservations();
        model.addAttribute(reservationList);
        model.addAttribute(account);
        return "admin/reservations";
    }
}
