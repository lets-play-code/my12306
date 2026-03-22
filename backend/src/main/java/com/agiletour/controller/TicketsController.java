package com.agiletour.controller;

import com.agiletour.dto.MyTicketResponse;
import com.agiletour.entity.User;
import com.agiletour.repo.TicketRepo;
import com.agiletour.support.CurrentTimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tickets")
public class TicketsController {

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private AuthenticatedUserSupport authenticatedUserSupport;

    @Autowired
    private CurrentTimeProvider currentTimeProvider;

    @GetMapping("/me")
    public List<MyTicketResponse> myTickets(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        User currentUser = authenticatedUserSupport.requiredUser(authorization);
        LocalDateTime now = currentTimeProvider.now();
        return ticketRepo.findByUserOrderByFromDepartureTime(currentUser).stream()
                .map(ticket -> MyTicketResponse.from(ticket, now))
                .collect(Collectors.toList());
    }
}
