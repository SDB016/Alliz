package com.alliz.account;

import com.alliz.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockBean JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원가입 - 비정상 입력")
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
                .andExpect(model().hasErrors());
    }

    @DisplayName("회원가입 - 정상 입력")
    @Test
    void signUp_correct() throws Exception {
        String email = "user@email.com";
        String password = "abc1234567";
        mockMvc.perform(post("/sign-up")
                        .with(csrf())
                        .param("nickname", "user")
                        .param("email", email)
                        .param("password", password)
                        .param("passwordConfirm", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail(email);
        assertNotNull(account);
        assertNotEquals(password, account.getPassword());
        assertTrue(passwordEncoder.matches(password, account.getPassword()));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}