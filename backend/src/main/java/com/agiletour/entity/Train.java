package com.agiletour.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    public int getRemainingTickets() {
        if (stops.isEmpty()) return 0;
        long fromId = stops.get(0).getId();
        long toId = stops.get(stops.size() - 1).getId();
        return (int) seats.stream().filter(seat -> seat.isAvailable((int) fromId, (int) toId)).count();
    }
}
