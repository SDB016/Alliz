package com.alliz.settings;

import com.alliz.WithAccount;
import com.alliz.account.AccountRepository;
import com.alliz.account.AccountService;
import com.alliz.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private PasswordEncoder passwordEncoder;

    @WithAccount("user")
    @DisplayName("프로필 업데이트 뷰 - 프로필")
    @Test
    void profile_update_form() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(get("/settings/profile"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"));
    }

    @WithAccount("user")
    @DisplayName("프로필 - 프로필 업데이트 - 성공")
    @Test
    void update_profile_success() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String phoneNum = "010-1234-1234";

        mockMvc.perform(post("/settings/profile")
                        .param("phone", phoneNum)
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"));

        assertEquals(phoneNum, account.getPhone());
    }

    @WithAccount("user")
    @DisplayName("프로필 - 프로필 업데이트 - 실패")
    @Test
    void update_profile_error() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String phoneNum = "12345678";

        mockMvc.perform(post("/settings/profile")
                        .param("phone", phoneNum)
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"));

        assertNotEquals(phoneNum, account.getPhone());
    }

    @WithAccount("user")
    @DisplayName("프로필 업데이트 뷰 - 비밀번호")
    @Test
    void password_update_form() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(get("/settings/password"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"));
    }

    @WithAccount("user")
    @DisplayName("프로필 - 비밀번호 업데이트 - 성공")
    @Test
    void update_password_success() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String oldEncodedPassword = account.getPassword();

        String newRawPassword = "abcd123123";
        mockMvc.perform(post("/settings/password")
                        .param("password", newRawPassword)
                        .param("passwordConfirm", newRawPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"));

        assertTrue(passwordEncoder.matches(newRawPassword,account.getPassword()));
        assertFalse(passwordEncoder.matches(oldEncodedPassword, account.getPassword()));
    }

    @WithAccount("user")
    @DisplayName("프로필 - 비밀번호 업데이트 - 실패")
    @Test
    void update_password_error() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String oldEncodedPassword = account.getPassword();

        String newRawPassword = "abcd123123";
        String wrongPasswordConfirm = "aaa123123";
        mockMvc.perform(post("/settings/password")
                        .param("password", newRawPassword)
                        .param("passwordConfirm", wrongPasswordConfirm)
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"));

        assertFalse(passwordEncoder.matches(newRawPassword,account.getPassword()));
        assertEquals(oldEncodedPassword, account.getPassword());
    }

    @WithAccount("user")
    @DisplayName("프로필 업데이트 뷰 - 알림")
    @Test
    void update_notifications_form() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(get("/settings/notifications"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attributeExists("notificationsForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/notifications"));
    }

    @WithAccount("user")
    @DisplayName("프로필 - 알림 업데이트 - 성공")
    @Test
    void update_notifications_success() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(post("/settings/notifications")
                        .param("childTakingByWeb", "true")
                        .param("childTakingByEmail", "false")
                        .param("childBringBackByWeb", "false")
                        .param("childBringBackByEmail", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"));

        assertTrue(account.isChildTakingByWeb());
        assertFalse(account.isChildTakingByEmail());
        assertFalse(account.isChildBringBackByWeb());
        assertTrue(account.isChildBringBackByEmail());
    }

    @WithAccount("user")
    @DisplayName("프로필 - 알림 업데이트 - 실패")
    @Test
    void update_notifications_error() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(post("/settings/notifications")
                        .param("childTakingByWeb", "abc")
                        .param("childTakingByEmail", "abc")
                        .param("childBringBackByWeb", "abc")
                        .param("childBringBackByEmail", "abc")
                        .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/notifications"));

        assertTrue(account.isChildTakingByWeb());
        assertFalse(account.isChildTakingByEmail());
        assertTrue(account.isChildBringBackByWeb());
        assertFalse(account.isChildBringBackByEmail());
    }

}