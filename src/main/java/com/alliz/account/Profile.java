package com.alliz.account;

import com.alliz.domain.Account;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.validation.constraints.Pattern;

@Data
public class Profile {

    @Pattern(regexp = "^(01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})|)$", message = "휴대폰 번호 형식에 맞지 않습니다.")
    private String phone;

    private String kakaoTalkId;

    private String location;

    public Profile() {
    }

    public Profile(Account account) {
        this.phone = account.getPhone();
        this.kakaoTalkId = account.getKakaoTalkId();
        this.location = account.getLocation();
    }
}
