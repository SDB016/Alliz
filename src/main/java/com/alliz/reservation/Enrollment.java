package com.alliz.reservation;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private List<EnrollmentReservation> enrollmentReservations = new ArrayList<>();

    private LocalDate enrollmentDate;

    public void connectChild(Child child) {
        this.child = child;
        child.getEnrollments().add(this);
    }

    public void addReservationDate(EnrollmentReservation enrollmentReservation) {
        this.getEnrollmentReservations().add(enrollmentReservation);
        enrollmentReservation.setEnrollment(this);
    }

    public static Enrollment createEnrollment(Child child, EnrollmentReservation... enrollmentReservations) {
        Enrollment enrollment = new Enrollment();
        enrollment.connectChild(child);
        for (EnrollmentReservation enrollmentReservation : enrollmentReservations) {
            enrollment.addReservationDate(enrollmentReservation);
        }
        enrollment.setEnrollmentDate(LocalDate.now());
        return enrollment;
    }
}
