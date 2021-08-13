package com.alliz.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"nickname","empty.nickname","닉네임을 입력하세요.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"password","empty.password","패스워드를 입력하세요.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","empty.email","이메일을 입력하세요.");

        SignUpForm signUpForm = (SignUpForm) target;
        if (!signUpForm.getPassword().equals(signUpForm.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm","invalid.passwordConfirm", new Object[]{signUpForm.getPasswordConfirm()},"입력한 패스워드와 다릅니다.");
        }
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email","invalid.email",new Object[]{signUpForm.getEmail()},"이미 사용중인 이메일입니다.");
        }
        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname","invalid.nickname",new Object[]{signUpForm.getNickname()},"이미 사용중인 닉네임입니다.");
        }
    }
}
