package com.alliz.reservation;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Enrollment {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Child child;

    private LocalDateTime enrollmentDateTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Reservation reservation;

    public void addChild(Child child) {
        this.child = child;
        child.getEnrollments().add(this);
    }

    private void addReservation(Reservation reservation) {
        this.setReservation(reservation);
        reservation.getEnrollmentList().add(this);
    }

    public static Enrollment createEnrollment(Child child, Reservation reservation) {
        Enrollment enrollment = new Enrollment();
        enrollment.addChild(child);
        enrollment.addReservation(reservation);
        enrollment.setEnrollmentDateTime(LocalDateTime.now());
        return enrollment;
    }
}
