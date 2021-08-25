package com.alliz.settings;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PasswordForm {

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{8,50}$", message = "8자 이상, 영어와 숫자를 최소 한 개 이상 사용하세요.")
    private String password;

    @NotBlank
    private String passwordConfirm;
}
