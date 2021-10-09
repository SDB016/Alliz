package com.alliz.account.dto;

import lombok.Data;

@Data
public class NotificationsForm {

    private boolean childTakingByWeb;

    private boolean childTakingByEmail;

    private boolean childBringBackByWeb;

    private boolean childBringBackByEmail;

}
