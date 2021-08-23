package com.alliz.settings;

import com.alliz.WithAccount;
import com.alliz.account.AccountRepository;
import com.alliz.account.AccountService;
import com.alliz.account.Profile;
import com.alliz.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @WithAccount("user")
    @DisplayName("프로필 업데이트 뷰 - 프로필")
    @Test
    void profile_update_form() throws Exception {
        Account account = accountRepository.findByNickname("user");

        mockMvc.perform(get("/settings/profile"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attributeExists("profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"));
    }

    @WithAccount("user")
    @DisplayName("프로필 - 프로필 업데이트 - 성공")
    @Test
    void update_profile_success() throws Exception {
        Account account = accountRepository.findByNickname("user");
        String phoneNum = "010-1234-1234";
        String kakaoId = "myId";

        mockMvc.perform(post("/settings/profile")
                        .param("phone", phoneNum)
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"));

        assertEquals(phoneNum, account.getPhone());

        mockMvc.perform(post("/settings/profile")
                        .param("kakaoTalkId", kakaoId)
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"));

        assertEquals(phoneNum, account.getPhone());
        assertEquals(kakaoId, account.getKakaoTalkId());
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

}