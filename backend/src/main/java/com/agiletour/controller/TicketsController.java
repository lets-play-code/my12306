package com.agiletour.controller;

import com.agiletour.entity.Ticket;
import com.agiletour.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class TicketsController {

    @Autowired
    private TicketRepo ticketRepo;

    @GetMapping(value = "/tickets")
    public List<Ticket> listTickets() {
        return ticketRepo.findAll();
    }
}
