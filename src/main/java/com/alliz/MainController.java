package com.alliz;

import com.alliz.account.AccountService;
import com.alliz.account.CurrentAccount;
import com.alliz.domain.Account;
import com.alliz.domain.Role;
import com.alliz.reservation.Reservation;
import com.alliz.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ReservationRepository reservationRepository;
    private final AccountService accountService;

    @GetMapping("/")
    public String home(@CurrentAccount Account currentAccount, Model model) {
        if (currentAccount != null) {
            Account account = accountService.getAccountWithChildren(currentAccount.getNickname());
            model.addAttribute(account);
            if (account.isAdmin()) {
                return "admin/index";
            }
            List<Reservation> reservationList = reservationRepository.findAll();
            model.addAttribute(reservationList);
            return "index";
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
