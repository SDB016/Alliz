package com.alliz.child;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class ChildForm {

    @Length(max = 8)
    private String name;

    @DateTimeFormat(pattern = "MM-dd")
    private LocalDate birth;

    @Pattern(regexp = "^(01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})|)$", message = "휴대폰 번호 형식에 맞지 않습니다.")
    private String phone;

    private String profileImage;

    @Length(max = 30)
    private String groupName;

    @Length(max = 30)
    private String schoolName;

}
