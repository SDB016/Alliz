package com.alliz.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationDate> reservationDates = new ArrayList<>();

    private LocalDate enrollmentDate;

    public void setAccount(Account account) {
        this.account = account;
        account.getReservations().add(this);
    }

    public void addReservationDate(ReservationDate reservationDate) {
        this.getReservationDates().add(reservationDate);
        reservationDate.setReservation(this);
    }

    public static Reservation createReservation(Account account, ReservationDate... reservationDates) {
        Reservation reservation = new Reservation();
        reservation.setAccount(account);
        for (ReservationDate reservationDate : reservationDates) {
            reservation.addReservationDate(reservationDate);
        }
        reservation.setEnrollmentDate(LocalDate.now());
        return reservation;
    }
}
