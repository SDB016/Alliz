package com.alliz.account.admin;

import com.alliz.domain.Account;
import com.alliz.domain.Role;
import com.alliz.reservation.Reservation;
import com.alliz.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ReservationRepository reservationRepository;

    public void checkRole(Account account) throws AccessDeniedException {
        if (!isAdmin(account)) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
    }

    private boolean isAdmin(Account account) {
        return account.getRole() == Role.ROLE_ADMIN;
    }

    public Reservation addNewReservation(ReservationForm form) {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(form.getReservationTime());
        reservation.setReservationLocation(form.getReservationLocation());
        return reservationRepository.save(reservation);
    }

    public boolean isAlreadyReservation(String reservationTime) {
        return reservationRepository.findByReservationTime(reservationTime) != null;
    }
}
