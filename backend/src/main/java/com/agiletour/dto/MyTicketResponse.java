package com.agiletour.dto;

import com.agiletour.entity.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyTicketResponse {
    private long id;
    private String trainName;
    private String fromStation;
    private String toStation;
    private LocalDateTime departureTime;
    private Status status;
    private String statusText;

    public enum Status {
        DEPARTED,
        UPCOMING_SOON,
        UPCOMING
    }

    public static MyTicketResponse from(Ticket ticket, LocalDateTime now) {
        Status status = resolveStatus(ticket.getFrom().getDepartureTime(), now);
        return MyTicketResponse.builder()
                .id(ticket.getId())
                .trainName(ticket.getSeat().getTrain().getName())
                .fromStation(ticket.getFrom().getName())
                .toStation(ticket.getTo().getName())
                .departureTime(ticket.getFrom().getDepartureTime())
                .status(status)
                .statusText(toStatusText(status))
                .build();
    }

    private static Status resolveStatus(LocalDateTime departureTime, LocalDateTime now) {
        if (departureTime.isBefore(now)) {
            return Status.DEPARTED;
        }
        if (!departureTime.isAfter(now.plusHours(3))) {
            return Status.UPCOMING_SOON;
        }
        return Status.UPCOMING;
    }

    private static String toStatusText(Status status) {
        switch (status) {
            case DEPARTED:
                return "已发车";
            case UPCOMING_SOON:
                return "即将发车";
            default:
                return "未发车";
        }
    }
}
