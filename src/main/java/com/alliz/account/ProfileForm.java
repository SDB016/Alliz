package com.alliz.account;

import com.alliz.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Pattern;

@Data
public class ProfileForm {

    @Pattern(regexp = "^(01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})|)$", message = "휴대폰 번호 형식에 맞지 않습니다.")
    private String phone;

    @Length(max = 30)
    private String kakaoTalkId;

    @Length(max = 50)
    private String location;

    private String profileImage;


}
