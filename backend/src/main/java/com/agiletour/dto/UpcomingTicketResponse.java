package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpcomingTicketResponse {
    private long id;
    private String trainName;
    private String departureTime;
    private String travelDate;
    private String fromStation;
    private String toStation;
    private int remainingMinutes;
}
