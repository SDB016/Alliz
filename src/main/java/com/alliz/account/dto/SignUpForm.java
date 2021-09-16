package com.alliz.account.dto;

import com.alliz.account.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$", message = "3 ~ 20자, 한글 또는 영어, 숫자를 이용한 닉네임을 입력하세요.")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{8,50}$", message = "8자 이상, 영어와 숫자를 최소 한 개 이상 사용하세요.")
    private String password;

    @NotBlank
    private String passwordConfirm;

    private Role role;
}

