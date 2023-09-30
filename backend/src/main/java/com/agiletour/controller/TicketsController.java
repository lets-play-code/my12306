package com.agiletour.controller;

import com.agiletour.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("api")
public class TicketsController {

    @Autowired
    private TicketRepo ticketRepo;

    @GetMapping(value = "/api/tickets", produces = "application/json")
    public String listTickets() {
//        var all = ticketRepo.findAll();
//        all.forEach(ticket -> ticket.setTrain(null));
        return "[]";
    }
}
