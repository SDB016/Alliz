package com.alliz.account;

import com.alliz.account.dto.NotificationsForm;
import com.alliz.account.dto.PasswordForm;
import com.alliz.account.dto.ProfileForm;
import com.alliz.account.dto.SignUpForm;
import com.alliz.child.Child;
import com.alliz.child.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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
    private final ModelMapper modelMapper;

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
                .role(signUpForm.getRole())
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
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(account.getRole().name()));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account, roles), // principle
                account.getPassword(),
                roles);
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

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(account.getRole().name()));

        return new UserAccount(account, roles);
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
        modelMapper.map(profileForm, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationsForm notificationsForm) {
        modelMapper.map(notificationsForm, account);
        accountRepository.save(account);
    }

    public Account getAccountWithChildren(String nickname) {
        Account byNickname = accountRepository.findAccountWithChildrenByNickname(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return byNickname;
    }

    public void updateBanner(Account account, String banner) {
        Account byNickname = accountRepository.findByNickname(account.getNickname());
        byNickname.setBanner(banner);
    }

    public void enableBanner(Account account) {
        account.setUseBanner(true);
    }

    public void disableBanner(Account account) {
        account.setUseBanner(false);
    }

    public Account getAccountByNickname(String nickname) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return byNickname;
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }
}
