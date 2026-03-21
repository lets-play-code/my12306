package com.agiletour.controller;

import com.agiletour.dto.TicketResponse;
import com.agiletour.entity.User;
import com.agiletour.repo.TicketRepo;
import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tickets")
public class MyTicketsController {

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public List<TicketResponse> getMyTickets(@RequestHeader(value = "Authorization", required = false) String auth) {
        long userId = extractUserId(auth);
        return ticketRepo.findByUserId(userId).stream()
                .map(TicketResponse::from)
                .collect(Collectors.toList());
    }

    private long extractUserId(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BadRequestException("Not authenticated");
        }
        String token = auth.substring(7);
        if (!token.startsWith("test-token-")) {
            throw new BadRequestException("Invalid token");
        }
        return Long.parseLong(token.substring("test-token-".length()));
    }
}
