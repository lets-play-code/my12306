package com.agiletour.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "seat")
@Getter
@Setter
public class Seat {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne
    private Train train;

    private String name;

    @OneToMany(mappedBy = "seat")
    private List<Ticket> tickets;

    public boolean isAvailable(int fromId, int toId) {
        List<Ticket> ticket = getTickets();
        if (ticket.isEmpty()) return true;
        return !isOverlap(ticket.get(0), fromId, toId);
    }

    private boolean isOverlap(Ticket ticket, int fromId, int toId) {
        Integer fromIndex = train.getStops().stream().filter(stop -> stop.getId() == fromId).map(Stop::getOrder).findFirst().get();
        Integer ticketToIndex = train.getStops().stream().filter(stop -> stop.getId() == ticket.getTo().getId()).map(Stop::getOrder).findFirst().get();
        Integer toIndex = train.getStops().stream().filter(stop -> stop.getId() == toId).map(Stop::getOrder).findFirst().get();
        Integer ticketFromIndex = train.getStops().stream().filter(stop -> stop.getId() == ticket.getFrom().getId()).map(Stop::getOrder).findFirst().get();
        return fromIndex < ticketToIndex && toIndex > ticketFromIndex;
    }
}
