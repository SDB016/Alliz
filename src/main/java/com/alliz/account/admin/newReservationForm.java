package com.alliz.account.admin;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class newReservationForm {

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime time;

    private String location;
}
