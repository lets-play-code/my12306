package com.agiletour.controller;

import com.agiletour.dto.TicketResponse;
import com.agiletour.dto.UpcomingTicketResponse;
import com.agiletour.entity.Ticket;
import com.agiletour.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tickets")
public class TicketController {

    @Autowired
    private TicketRepo ticketRepo;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/my")
    public List<TicketResponse> getMyTickets(HttpServletRequest request) {
        long userId = (Long) request.getAttribute("userId");
        List<Ticket> tickets = ticketRepo.findByUserIdOrderByTravelDateAsc(userId);

        return tickets.stream().map(ticket -> {
            String departureTime = ticket.getSeat().getTrain().getDepartureTime().format(TIME_FORMATTER);
            return new TicketResponse(
                ticket.getId(),
                ticket.getSeat().getTrain().getName(),
                departureTime,
                ticket.getTravelDate().toString(),
                ticket.getFrom().getName(),
                ticket.getTo().getName()
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/upcoming")
    public List<UpcomingTicketResponse> getUpcomingTickets(HttpServletRequest request) {
        long userId = (Long) request.getAttribute("userId");
        List<Ticket> tickets = ticketRepo.findByUserIdOrderByTravelDateAsc(userId);

        LocalDateTime now = LocalDateTime.now();

        return tickets.stream()
            .filter(ticket -> {
                LocalDate travelDate = ticket.getTravelDate();
                LocalTime departureTime = ticket.getSeat().getTrain().getDepartureTime();

                // 计算发车时间点
                LocalDateTime departureDateTime = LocalDateTime.of(travelDate, departureTime);

                // 只返回未来的车次
                if (departureDateTime.isBefore(now)) {
                    return false;
                }

                // 计算剩余分钟数
                long minutesUntilDeparture = java.time.Duration.between(now, departureDateTime).toMinutes();

                // 只返回 3 小时（180 分钟）内的车次
                return minutesUntilDeparture <= 180;
            })
            .map(ticket -> {
                LocalDate travelDate = ticket.getTravelDate();
                LocalTime departureTime = ticket.getSeat().getTrain().getDepartureTime();
                LocalDateTime departureDateTime = LocalDateTime.of(travelDate, departureTime);
                int remainingMinutes = (int) java.time.Duration.between(now, departureDateTime).toMinutes();

                String depTime = departureTime.format(TIME_FORMATTER);
                return new UpcomingTicketResponse(
                    ticket.getId(),
                    ticket.getSeat().getTrain().getName(),
                    depTime,
                    travelDate.toString(),
                    ticket.getFrom().getName(),
                    ticket.getTo().getName(),
                    Math.max(0, remainingMinutes)
                );
            })
            .collect(Collectors.toList());
    }
}
