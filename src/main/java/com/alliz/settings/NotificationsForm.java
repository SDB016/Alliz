package com.alliz.settings;

import com.alliz.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NotificationsForm {

    private boolean childTakingByWeb;

    private boolean childTakingByEmail;

    private boolean childBringBackByWeb;

    private boolean childBringBackByEmail;

}
