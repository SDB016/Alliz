package com.alliz.account.oauth;

import com.alliz.account.Account;
import lombok.Getter;

import java.io.Serializable;

/**
 * 직렬화 기능을 가진 Account 클래스
 */
@Getter
public class SessionAccount implements Serializable {
    private String nickname;
    private String email;
    private String profileImage;

    public SessionAccount(Account account) {
        this.nickname = account.getNickname();
        this.email = account.getEmail();
        this.profileImage = account.getProfileImage();
    }
}
