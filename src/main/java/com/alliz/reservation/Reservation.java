package com.alliz.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue
    private Long id;

    private String reservationTime;

    private String reservationLocation;

    @OneToMany(mappedBy = "reservation")
    private List<EnrollmentReservation> enrollmentReservations = new ArrayList<>();
}
