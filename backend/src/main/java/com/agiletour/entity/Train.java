package com.agiletour.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @OrderBy("`order`")
    private List<Stop> stops = new ArrayList<>();

    public Stop findStop(int id) {
        return stops.stream().filter(stop -> stop.getId() == id).findFirst().get();
    }
}
