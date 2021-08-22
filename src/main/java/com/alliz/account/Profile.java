package com.alliz.account;

import com.alliz.domain.Account;
import lombok.Data;

@Data
public class Profile {

    private String phone;

    private String kakaoTalkId;

    private String location;

    public Profile(Account account) {
        this.phone = account.getPhone();
        this.kakaoTalkId = account.getKakaoTalkId();
        this.location = account.getLocation();
    }
}
