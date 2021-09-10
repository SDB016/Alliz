package com.alliz.account;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ChildRepository childRepository;
    private final ChildService childService;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/sign-up/children";
    }

    @GetMapping("/sign-up/children")
    public String signUpChild(@CurrentAccount Account account, Model model) {
        Account accountWithChildren = accountService.getAccountWithChildren(account.getNickname());
        model.addAttribute(accountWithChildren);
        Set<Child> children = accountService.getChild(accountWithChildren);
        model.addAttribute("children", children.stream().map(Child::getName).collect(Collectors.toList()));
        return "account/sign-up-children";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }
        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("account", account);
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        if (account == null) {
            model.addAttribute("error", "wrong.account");
            return "account/check-email";
        }
        model.addAttribute("email", account.getEmail());
        model.addAttribute("account", account);
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendCheckEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error",
                    "인증 이메일은 30분에 한번만 보낼 수 있습니다. 잠시후 다시 시도해주세요.");
            model.addAttribute("email", account.getEmail());
            model.addAttribute("account", account);
            return "account/check-email";
        }
        accountService.sendSignUpConfirmEmail(account);
        model.addAttribute("message", "done.resend");
        model.addAttribute("email", account.getEmail());
        model.addAttribute("account", account);
        return "account/check-email";
    }

    @PostMapping("/account/child/add")
    @ResponseBody
    public ResponseEntity addChild(@CurrentAccount Account account, @RequestBody ChildForm childForm) {
        Child child = childService.saveChild(childForm);
        accountService.addChild(account, child);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/account/child/remove")
    @ResponseBody
    public ResponseEntity removeChild(@CurrentAccount Account account, @RequestBody ChildForm childForm) {
        Child child = childService.findChild(childForm);
        accountService.removeChild(account, child);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account byNickname = accountService.getAccountWithChildren(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute("isOwner", byNickname.equals(account));
        model.addAttribute("account", byNickname);
        return "account/profile";
    }

    @GetMapping("/profile/{nickname}/children")
    public String viewChildren(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account byNickname = accountService.getAccountWithChildren(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute("isOwner", byNickname.equals(account));
        model.addAttribute("account", byNickname);
        return "account/children";
    }
/*
    @GetMapping("/reservation/{reservationId}/enroll/{accountId}")
    public String enrollReservation(@PathVariable Long reservationId, @PathVariable Long accountId, @CurrentAccount Account account) {

    }*/
}
