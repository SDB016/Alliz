package com.alliz.account;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import com.alliz.settings.NotificationsForm;
import com.alliz.settings.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ChildRepository childRepository;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .childBringBackByWeb(true)
                .childTakingByWeb(true)
                .children(new HashSet<>())
                .build();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        newAccount.generateEmailCheckToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("알리지, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), // principle
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public void addChild(Account account, Child child) {
        Account byNickname = accountRepository.findByNickname(account.getNickname());
        if (byNickname == null) {
            throw new IllegalArgumentException(account.getNickname() + "에 해당하는 유저가 없습니다.");
        }
        child.setAccount(byNickname); // TODO 좀 더 쿼리 최적화 할 수 없을까?
    }

    public void removeChild(Account account, Child child) {
        Account byNickname = this.getAccountWithChildren(account.getNickname()); // TODO child를 같이 가져오는게 더 좋을까??
        if (byNickname == null) {
            throw new IllegalArgumentException(account.getNickname() + "에 해당하는 유저가 없습니다.");
        }
        byNickname.getChildren().remove(child);
        childRepository.delete(child);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public Set<Child> getChild(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getChildren();
    }

    public void updateProfile(Account account, ProfileForm profileForm) {
        account.setPhone(profileForm.getPhone());
        account.setKakaoTalkId(profileForm.getKakaoTalkId());
        account.setLocation(profileForm.getLocation());
        account.setProfileImage(profileForm.getProfileImage());
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationsForm notificationsForm) {
        account.setChildTakingByWeb(notificationsForm.isChildTakingByWeb());
        account.setChildTakingByEmail(notificationsForm.isChildTakingByEmail());
        account.setChildBringBackByWeb(notificationsForm.isChildBringBackByWeb());
        account.setChildBringBackByEmail(notificationsForm.isChildBringBackByEmail());
        accountRepository.save(account);
    }

    public Account getAccountWithChildren(String nickname) {
        return accountRepository.findAccountWithChildrenByNickname(nickname);
    }
}
