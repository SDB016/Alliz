package com.alliz.reservation;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.alliz.reservation.Reservation.LOCALDATETIMEFORMAT;

@Data
public class ReservationForm {

    private String reservationDateTime;

    private String reservationLocation;

    public LocalDateTime getReservationDateTimeAsDateTime() {
        return LocalDateTime.parse(this.reservationDateTime, DateTimeFormatter.ofPattern(LOCALDATETIMEFORMAT));
    }

}
