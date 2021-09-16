package com.alliz.settings;

import com.alliz.account.WithAccount;
import com.alliz.account.AccountRepository;
import com.alliz.account.AccountService;
import com.alliz.child.ChildRepository;
import com.alliz.account.Account;
import com.alliz.child.Child;
import com.alliz.account.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;

import java.util.HashSet;

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
    @Autowired private ChildRepository childRepository;

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

    @WithAccount("user")
    @DisplayName("학생 세부 정보 조회 - 보호자")
    @Test
    void get_child_settings_view_parent() throws Exception {
        Account account = accountService.getAccountByNickname("user");
        Child child = makeChildWithParent(account);

        mockMvc.perform(get("/settings/child/" + child.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("child"))
                .andExpect(model().attributeExists("childForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/child"));
    }

    @WithAccount("user")
    @DisplayName("학생 세부 정보 조회 - 제 3자")
    @Test
    void get_child_settings_view_stranger() throws Exception {
        Account stranger = makeStranger();
        accountRepository.save(stranger);
        Child child = makeChildWithParent(stranger);

        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(get("/settings/child/" + child.getId())));
    }

    @WithAccount("user")
    @DisplayName("학생 정보 수정 - 보호자")
    @Test
    void update_child_profile_parent() throws Exception {
        Account account = accountRepository.findByNickname("user");
        Child child = makeChildWithParent(account);

        String newName = "newName";
        String newBirth = "2000-01-01";
        String newPhone = "010-1234-1234";
        mockMvc.perform(post("/settings/child/" + child.getId())
                .param("name", newName)
                .param("birth", newBirth)
                .param("phone", newPhone)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/"+account.getNickname()+"/children"));

        assertEquals(child.getName(), newName);
        assertEquals(child.getBirth().toString(), newBirth);
        assertEquals(child.getPhone(), newPhone);
    }

    @WithAccount("user")
    @DisplayName("학생 정보 수정 - 제 3자")
    @Test
    void update_child_profile_stranger() throws Exception {
        Account stranger = makeStranger();
        accountRepository.save(stranger);
        Child child = makeChildWithParent(stranger);

        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(post("/settings/child/" + child.getId())
                        .param("name", "newName")
                        .param("birth", "2000-01-01")
                        .param("groupName", "newGroup")
                        .with(csrf())));
    }

    @WithAccount("user")
    @DisplayName("학생 정보 수정 - 에러")
    @Test
    void update_child_profile_error() throws Exception {
        Account account = accountRepository.findByNickname("user");
        Child child = makeChildWithParent(account);

        mockMvc.perform(post("/settings/child/" + child.getId())
                        .param("name", "newName")
                        .param("phone", "123-3456-7890")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("child"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("settings/child"));
    }

    private Child makeChildWithParent(Account parent) {
        Child child = Child.builder().name("AChild").build();
        child.setAccount(parent);
        childRepository.save(child);
        return child;
    }

    private Account makeStranger() {
        return Account.builder().nickname("stranger").email("email@email.com")
                .password("abc123123").role(Role.ROLE_USER).children(new HashSet<>()).build();
    }
}