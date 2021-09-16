package com.alliz.account;

import com.alliz.account.dto.SignUpForm;
import com.alliz.account.dto.SignUpFormValidator;
import com.alliz.child.Child;
import com.alliz.child.ChildForm;
import com.alliz.child.ChildRepository;
import com.alliz.child.ChildService;
import com.alliz.reservation.Reservation;
import com.alliz.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.Map;
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
    private final ReservationRepository reservationRepository;

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

    @PostMapping("/enroll")
    public String enroll(@CurrentAccount Account currentAccount,
                         @RequestBody Map<String, String> param, RedirectAttributes attributes) {
        long childId = Long.parseLong(param.get("childId"));
        long reservationId = Long.parseLong(param.get("reservationId"));

        Child child = childRepository.findById(childId).orElseThrow();
        if (!childService.isParent(child, currentAccount)) {
            try {
                throw new AccessDeniedException("보호자만 본인의 학생 예약을 할 수 있습니다.");
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }
        Reservation reservation = childService.enroll(child, reservationId);
        attributes.addFlashAttribute("message",
                "[장소: " + reservation.getReservationLocation()+", 시간: "+reservation.getReservationDateTime()+"] 배차가 예약됐습니다.");
        return "redirect:/";
    }
}
