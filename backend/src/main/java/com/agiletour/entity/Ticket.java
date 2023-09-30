package com.agiletour.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Ticket {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne
    private Train train;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        AVAILABLE, SOLD
    }
}
