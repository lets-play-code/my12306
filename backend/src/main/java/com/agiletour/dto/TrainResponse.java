package com.agiletour.dto;

import com.agiletour.entity.Stop;
import com.agiletour.entity.Train;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainResponse {
    private Long id;
    private String name;
    private List<Stop> stops = new ArrayList<>();
    private int remainingTickets;

    public static TrainResponse from(Train train, Integer fromStopId, Integer toStopId) {
        TrainResponse response = TrainResponse.builder()
                .id(train.getId())
                .name(train.getName())
                .stops(train.getStops())
                .build();

        // Calculate remaining tickets based on query parameters
        if (fromStopId != null && toStopId != null) {
            response.setRemainingTickets(train.getRemainingTickets(fromStopId, toStopId));
        } else {
            // Default to full route (first to last stop) if no parameters provided
            if (!train.getStops().isEmpty()) {
                int firstStopId = (int) train.getStops().get(0).getId();
                int lastStopId = (int) train.getStops().get(train.getStops().size() - 1).getId();
                response.setRemainingTickets(train.getRemainingTickets(firstStopId, lastStopId));
            } else {
                response.setRemainingTickets(0);
            }
        }

        return response;
    }
}
