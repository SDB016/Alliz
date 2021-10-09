package com.alliz.enrollment;

import com.alliz.child.Child;
import com.alliz.reservation.Reservation;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Enrollment {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Child child;

    private LocalDateTime enrollmentDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
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
