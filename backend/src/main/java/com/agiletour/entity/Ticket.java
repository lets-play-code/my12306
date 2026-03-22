package com.agiletour.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Ticket {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @OneToOne
    private Seat seat;

    @OneToOne
    private Stop from;

    @OneToOne
    private Stop to;

    @ManyToOne
    private User user;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;
}
