package com.alliz.account.validator;

import com.alliz.account.dto.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password","empty.password","패스워드를 입력하세요.");

        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getPassword().equals(passwordForm.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm","invalid.passwordConfirm",new Object[]{passwordForm.getPasswordConfirm()},"입력한 패스워드와 다릅니다.");
        }
    }
}
