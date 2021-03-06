package com.alliz.settings;

import com.alliz.account.*;
import com.alliz.account.Account;
import com.alliz.account.dto.NicknameForm;
import com.alliz.account.dto.NotificationsForm;
import com.alliz.account.dto.PasswordForm;
import com.alliz.account.dto.ProfileForm;
import com.alliz.account.validator.NicknameValidator;
import com.alliz.account.validator.PasswordFormValidator;
import com.alliz.child.Child;
import com.alliz.child.ChildForm;
import com.alliz.child.ChildRepository;
import com.alliz.child.ChildService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final PasswordFormValidator passwordFormValidator;
    private final NicknameValidator nicknameValidator;
    private final ChildService childService;
    private final ChildRepository childRepository;
    private final ModelMapper modelMapper;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping("/settings/profile")
    public String updateProfileForm(@CurrentAccount Account currentAccount, Model model) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, ProfileForm.class));
        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentAccount Account currentAccount, @Valid ProfileForm profileForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(currentAccount);
            return "settings/profile";
        }

        accountService.updateProfile(currentAccount, profileForm);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/password")
    public String updatePasswordForm(@CurrentAccount Account currentAccount, Model model) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@CurrentAccount Account currentAccount, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(currentAccount);
            return "settings/password";
        }

        accountService.updatePassword(currentAccount, passwordForm);
        attributes.addFlashAttribute("message", "??????????????? ??????????????????.");
        return "redirect:/settings/password";
    }

    @GetMapping("/settings/notifications")
    public String updateNotificationsForm(@CurrentAccount Account currentAccount, Model model) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NotificationsForm.class));
        return "settings/notifications";
    }

    @PostMapping("/settings/notifications")
    public String updateNotifications(@CurrentAccount Account currentAccount, @Valid NotificationsForm notificationsForm, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(currentAccount);
            return "settings/notifications";
        }

        accountService.updateNotifications(currentAccount, notificationsForm);
        attributes.addFlashAttribute("message", "?????? ????????? ??????????????????.");
        return "redirect:/settings/notifications";
    }

    @GetMapping("/settings/banner")
    public String updateBannerForm(@CurrentAccount Account currentAccount, Model model) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        model.addAttribute(account);
        return "settings/banner";
    }

    @PostMapping("/settings/banner")
    public String updateBanner(@CurrentAccount Account currentAccount, String banner, RedirectAttributes attributes) {
        accountService.updateBanner(currentAccount, banner);
        attributes.addFlashAttribute("message", "?????? ???????????? ??????????????????.");
        return "redirect:/settings/banner";
    }

    @PostMapping("/settings/banner/enable")
    public String enableBanner(@CurrentAccount Account currentAccount) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        accountService.enableBanner(account);
        return "redirect:/settings/banner";
    }

    @PostMapping("/settings/banner/disable")
    public String disableBanner(@CurrentAccount Account currentAccount) {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        accountService.disableBanner(account);
        return "redirect:/settings/banner";
    }

    @GetMapping("/settings/child/{id}")
    public String childSettingsView(@CurrentAccount Account currentAccount, @PathVariable Long id, Model model) throws IllegalAccessException {
        Account account = accountService.getAccountByNickname(currentAccount.getNickname());
        Child child = childRepository.findById(id).orElseThrow();
        checkParent(account, child);
        model.addAttribute(account);
        model.addAttribute(child);
        model.addAttribute(modelMapper.map(child, ChildForm.class));
        return "settings/child";
    }

    @PostMapping("/settings/child/{id}")
    public String updateChildProfile(@CurrentAccount Account currentAccount, @PathVariable Long id, @Valid ChildForm childForm, Errors errors,
                                     Model model) throws IllegalAccessException {
        Child child = childRepository.findById(id).orElseThrow();
        checkParent(currentAccount, child);
        if (errors.hasErrors()) {
            model.addAttribute(child);
            model.addAttribute(currentAccount);
            return "settings/child";
        }

        childService.updateProfile(child, childForm);
        return "redirect:/profile/" + currentAccount.getNickname() + "/children";
    }

    @GetMapping("/settings/account")
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/settings/account";
    }
    private void checkParent(Account currentAccount, Child child) throws IllegalAccessException {
        if (!child.getAccount().getId().equals(currentAccount.getId())) {
            throw new IllegalAccessException("???????????? ????????? ????????? ??? ????????????.");
        }
    }
}
