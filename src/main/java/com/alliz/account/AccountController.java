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


@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ChildRepository childRepository;

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
    public String signUpChild(Model model) {
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
        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        if (account == null) {
            model.addAttribute("error", "wrong.account");
            return "account/check-email";
        }
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendCheckEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error",
                    "인증 이메일은 30분에 한번만 보낼 수 있습니다. 잠시후 다시 시도해주세요.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }
        accountService.sendSignUpConfirmEmail(account);
        model.addAttribute("message", "done.resend");
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute("isOwner", byNickname.equals(account));
        model.addAttribute("account", byNickname);
        return "account/profile";
    }

    @PostMapping("/account/child/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody ChildForm childForm) {
        Child child = childRepository.save(Child.builder().name(childForm.getChildName()).build());

        accountService.addChild(account, child);
        return ResponseEntity.ok().build();
    }
}
