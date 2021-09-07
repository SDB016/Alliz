package com.alliz.account.admin;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ReservationForm {

    private String reservationTime;

    private String reservationLocation;
}
