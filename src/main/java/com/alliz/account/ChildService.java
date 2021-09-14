package com.alliz.account;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import com.alliz.reservation.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildService {
    private final ChildRepository childRepository;
    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Child saveChild(ChildForm childForm) {
        return childRepository.save(Child.builder().name(childForm.getName()).build());
    }

    public Child findChild(ChildForm childForm) {
        Child child = childRepository.findByName(childForm.getName());
        if (child == null) {
            throw new IllegalArgumentException(childForm.getName() + "에 해당하는 학생이 없습니다.");
        }
        return child;
    }

    public void updateProfile(Child child, ChildForm childForm) {
        modelMapper.map(childForm, child);
        childRepository.save(child);
    }

    public boolean isParent(Child child, Account account) {
        return child.getAccount().getId().equals(account.getId());
    }

    public Reservation enroll(Child child, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        Enrollment enrollment = Enrollment.createEnrollment(child, reservation);

        enrollmentRepository.save(enrollment);
        return reservation;
    }
}
