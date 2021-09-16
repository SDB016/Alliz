package com.alliz.account;

import com.alliz.account.dto.SignUpForm;
import com.alliz.child.Child;
import com.alliz.child.ChildForm;
import com.alliz.child.ChildRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountService accountService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ChildRepository childRepository;

    @MockBean JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원가입 - 비정상")
    @Test
    void signUp_error() throws Exception {
        mockMvc.perform(post("/sign-up")
                .with(csrf())
                .param("nickname", "user")
                .param("email", "user@email.com")
                .param("password", "123")
                .param("passwordConfirm", "111"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().hasErrors())
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 - 정상")
    @Test
    void signUp_correct() throws Exception {
        String email = "user@email.com";
        String password = "abc1234567";
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "user")
                        .param("email", email)
                        .param("password", password)
                        .param("passwordConfirm", password)
                        .param("role",Role.ROLE_USER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sign-up/children"))
                .andExpect(view().name("redirect:/sign-up/children"))
                .andExpect(authenticated().withUsername("user"));

        Account account = accountRepository.findByEmail(email);
        assertNotNull(account);
        assertNotEquals(password, account.getPassword());
        assertNotNull(account.getEmailCheckToken());
        assertTrue(passwordEncoder.matches(password, account.getPassword()));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @WithAccount("user")
    @DisplayName("회원가입 - 학생 추가 화면 보이는지 테스트")
    @Test
    void signUp_child_view() throws Exception {
        mockMvc.perform(get("/sign-up/children"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up-children"));
    }

    @WithAccount("user")
    @DisplayName("회원가입 - 학생 추가")
    @Test
    void signUp_add_child() throws Exception {
        ChildForm childForm = new ChildForm();
        String childName = "testChild";
        childForm.setName(childName);

        mockMvc.perform(post("/account/child/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childForm)))
                .andExpect(status().isOk());


        Child child = childRepository.findByName(childName);
        assertNotNull(child);
        Account account = accountRepository.findByNickname("user");
        assertTrue(account.getChildren().contains(child));
        assertEquals(1, account.getChildren().size());
    }

    @WithAccount("user")
    @DisplayName("회원가입 - 학생 삭제")
    @Test
    void signUp_remove_child() throws Exception {
        Account account = accountRepository.findByNickname("user");
        Child child = childRepository.save(Child.builder().name("testChild").build());
        accountService.addChild(account, child);

        assertTrue(account.getChildren().contains(child));

        ChildForm childForm = new ChildForm();
        childForm.setName("testChild");

        mockMvc.perform(post("/account/child/remove")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(childForm)))
                .andExpect(status().isOk());

        assertFalse(account.getChildren().contains(child));
        assertEquals(0, account.getChildren().size());
    }

    @DisplayName("회원가입 후 토큰 확인 - 정상")
    @Test
    void signUp_email_check_token_verified_correct() throws Exception {
        SignUpForm signUpForm = createSignUpForm();
        Account account = accountService.processNewAccount(signUpForm);

        mockMvc.perform(get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("account"))
                .andExpect(authenticated().withUsername("user"));

        assertTrue(account.isEmailVerified());
        assertNotNull(account.getJoinedAt());
    }

    @DisplayName("회원가입 후 토큰 확인 - 비정상")
    @Test
    void signUp_email_check_token_verified_error() throws Exception {
        SignUpForm signUpForm = createSignUpForm();
        Account account = accountService.processNewAccount(signUpForm);

        mockMvc.perform(get("/check-email-token")
                        .param("token", "abcabc")
                        .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attribute("error","wrong.token"))
                .andExpect(unauthenticated());

        assertFalse(account.isEmailVerified());
        assertNull(account.getJoinedAt());
    }

    @WithAccount("user")
    @DisplayName("인증 이메일 재전송 화면 보이는지 확인 - 정상")
    @Test
    void check_email_correct() throws Exception {
        mockMvc.perform(get("/check-email"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));
    }

    @DisplayName("인증 이메일 재전송 화면 보이는지 확인 - 비정상")
    @Test
    void check_email_error() throws Exception {
        mockMvc.perform(get("/check-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));
    }

    @WithAccount("user")
    @DisplayName("인증 이메일 재전송 - 비정상 (시간 안 지남)")
    @Test
    void resend_check_email_error() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String beforeToken = account.getEmailCheckToken();
        LocalDateTime beforeTokenGeneratedAt = account.getEmailCheckTokenGeneratedAt();

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("message"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));

        String afterToken = account.getEmailCheckToken();
        LocalDateTime afterTokenGeneratedAt = account.getEmailCheckTokenGeneratedAt();

        assertEquals(beforeToken, afterToken);
        assertEquals(beforeTokenGeneratedAt, afterTokenGeneratedAt);
    }

    @WithAccount("user")
    @DisplayName("인증 이메일 재전송 - 비정상 (시간 지남)")
    @Test
    void resend_check_email_correct() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String beforeToken = account.getEmailCheckToken();
        account.setEmailCheckTokenGeneratedAt(account.getEmailCheckTokenGeneratedAt().minusMinutes(31));
        LocalDateTime beforeTokenGeneratedAt = account.getEmailCheckTokenGeneratedAt();

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));

        String afterToken = account.getEmailCheckToken();
        LocalDateTime afterTokenGeneratedAt = account.getEmailCheckTokenGeneratedAt();

        assertNotEquals(beforeToken, afterToken);
        assertNotEquals(beforeTokenGeneratedAt, afterTokenGeneratedAt);
    }

    @WithAccount("user")
    @DisplayName("프로필 뷰")
    @Test
    void profile_view() throws Exception{
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(get("/profile/" + account.getNickname()))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/profile"));
    }

    @WithAccount("user")
    @DisplayName("관리하는 학생 리스트 뷰")
    @Test
    void children_view() throws Exception {
        Account account = accountService.getAccountWithChildren("user");

        mockMvc.perform(get("/profile/" + account.getNickname() + "/children"))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/children"));
    }

    private SignUpForm createSignUpForm() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("user");
        signUpForm.setEmail("user@email.com");
        signUpForm.setPassword("abc1234567");
        signUpForm.setPasswordConfirm("abc1234567");
        signUpForm.setRole(Role.ROLE_USER);
        return signUpForm;
    }
}