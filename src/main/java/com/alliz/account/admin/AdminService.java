package com.alliz.account.admin;

import com.alliz.domain.Account;
import com.alliz.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {


    public void checkRole(Account account) throws AccessDeniedException {
        if (!isAdmin(account)) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
    }

    private boolean isAdmin(Account account) {
        return account.getRole() == Role.ROLE_ADMIN;
    }
}
