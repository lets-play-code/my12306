package com.agiletour.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Train train;

    public String getTrainName() {
        return train.getName();
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        AVAILABLE, SOLD
    }
}
