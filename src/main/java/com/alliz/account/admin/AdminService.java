package com.alliz.account.admin;

import com.alliz.domain.Account;
import com.alliz.domain.Role;
import com.alliz.reservation.Reservation;
import com.alliz.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    public void checkRole(Account account) {
        if (!isAdmin(account)) {
            try {
                throw new AccessDeniedException("관리자 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAdmin(Account account) {
        return account.getRole() == Role.ROLE_ADMIN;
    }

    public Reservation addNewReservation(ReservationForm form) {
        Reservation reservation = modelMapper.map(form, Reservation.class);
        return reservationRepository.save(reservation);
    }

    public boolean isAlreadyReservation(String reservationTime) {
        return reservationRepository.findByReservationTime(reservationTime) != null;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void removeReservationById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public void editReservation(Long id, ReservationForm form) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        modelMapper.map(form, reservation);
    }
}
