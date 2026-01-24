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

    @Transient
    private Integer queryFromStopId;

    @Transient
    private Integer queryToStopId;

    public Stop findStop(int id) {
        return stops.stream().filter(stop -> stop.getId() == id).findFirst().get();
    }

    @JsonProperty
    public int getRemainingTickets() {
        if (stops.isEmpty()) return 0;
        
        // 如果设置了查询参数，使用查询参数计算余票
        long fromId = (queryFromStopId != null) ? queryFromStopId : stops.get(0).getId();
        long toId = (queryToStopId != null) ? queryToStopId : stops.get(stops.size() - 1).getId();
        
        return (int) seats.stream().filter(seat -> seat.isAvailable((int) fromId, (int) toId)).count();
    }

    public int getRemainingTickets(int fromStopId, int toStopId) {
        return (int) seats.stream().filter(seat -> seat.isAvailable(fromStopId, toStopId)).count();
    }
}
