package com.alliz;

import com.alliz.account.CurrentAccount;
import com.alliz.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }


    @GetMapping("/admin")
    public String viewAdminHome(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
