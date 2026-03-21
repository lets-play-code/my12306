package com.agiletour.dto;

import com.agiletour.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String trainName;
    private String fromStation;
    private String toStation;
    private String departureTime;
    private String seatName;

    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .trainName(ticket.getFrom().getTrain().getName())
                .fromStation(ticket.getFrom().getName())
                .toStation(ticket.getTo().getName())
                .departureTime(ticket.getFrom().getDepartureTime())
                .seatName(ticket.getSeat().getName())
                .build();
    }
}
