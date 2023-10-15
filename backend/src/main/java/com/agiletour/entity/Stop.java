package com.agiletour.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
public class Stop {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @JsonIgnore
    @Column(name = "`order`")
    private int order;

    @ManyToOne
    @JsonIgnore
    private Train train;

    private String name;

}
