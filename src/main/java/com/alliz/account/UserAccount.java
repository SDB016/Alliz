package com.alliz.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getNickname(), account.getPassword(), authorities);
        this.account = account;
    }
}
