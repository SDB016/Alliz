package com.alliz.reservation;

import com.alliz.domain.Account;
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
    private Account account;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private List<EnrollmentReservation> enrollmentReservations = new ArrayList<>();

    private LocalDate enrollmentDate;

    public void setAccount(Account account) {
        this.account = account;
        account.getEnrollments().add(this);
    }

    public void addReservationDate(EnrollmentReservation enrollmentReservation) {
        this.getEnrollmentReservations().add(enrollmentReservation);
        enrollmentReservation.setEnrollment(this);
    }

    public static Enrollment createEnrollment(Account account, EnrollmentReservation... enrollmentReservations) {
        Enrollment enrollment = new Enrollment();
        enrollment.setAccount(account);
        for (EnrollmentReservation enrollmentReservation : enrollmentReservations) {
            enrollment.addReservationDate(enrollmentReservation);
        }
        enrollment.setEnrollmentDate(LocalDate.now());
        return enrollment;
    }
}
