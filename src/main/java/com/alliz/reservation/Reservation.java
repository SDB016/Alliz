package com.alliz.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Reservation {

    public static final String LOCALDATETIMEFORMAT = "yyyy-MM-dd HH:mm";

    @Id @GeneratedValue
    private Long id;

    @DateTimeFormat(pattern = LOCALDATETIMEFORMAT)
    private LocalDateTime reservationDateTime;

    private String reservationLocation;

    @OneToMany(mappedBy = "reservation")
    private List<Enrollment> enrollmentList = new ArrayList<>();

    public String getReservationDateTimeAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCALDATETIMEFORMAT);
        return this.reservationDateTime.format(formatter);
    }
}
