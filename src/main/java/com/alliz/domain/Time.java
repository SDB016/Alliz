package com.alliz.domain;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Time {

    @Id @GeneratedValue
    private Long id;

    private LocalDateTime reservationTime;

    @OneToMany(mappedBy = "time")
    private List<ReservationDate> reservationDates = new ArrayList<>();
}
