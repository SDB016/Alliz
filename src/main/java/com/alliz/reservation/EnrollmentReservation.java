package com.alliz.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentReservation {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public static EnrollmentReservation createEnrollmentReservation(Reservation reservation) {
        EnrollmentReservation enrollmentReservation = new EnrollmentReservation();
        enrollmentReservation.setReservation(reservation);

        return enrollmentReservation;
    }
}
