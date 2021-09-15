package com.alliz.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue
    private Long id;

    private LocalDateTime reservationTime;

    private LocalDate reservationDate;

    private String reservationLocation;

    @OneToMany(mappedBy = "reservation")
    private List<Enrollment> enrollmentList = new ArrayList<>();
}
