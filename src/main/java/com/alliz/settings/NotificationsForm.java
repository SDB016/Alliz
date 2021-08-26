package com.alliz.settings;

import com.alliz.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationsForm {

    private boolean childTakingByWeb;

    private boolean childTakingByEmail;

    private boolean childBringBackByWeb;

    private boolean childBringBackByEmail;

    public NotificationsForm(Account account) {
        this.childTakingByWeb = account.isChildTakingByWeb();
        this.childTakingByEmail = account.isChildTakingByEmail();
        this.childBringBackByWeb = account.isChildBringBackByWeb();
        this.childBringBackByEmail = account.isChildBringBackByEmail();
    }
}
