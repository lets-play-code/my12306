package com.agiletour.controller;

import com.agiletour.entity.Ticket;
import com.agiletour.entity.Train;
import com.agiletour.repo.TicketRepo;
import com.agiletour.repo.TrainRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class TrainsController {

    @Autowired
    private TrainRepo trainRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @PostMapping("/trains/{trainId}/tickets")
    public void buyTicket(@PathVariable long trainId) {
        var train = trainRepo.findById(trainId);
        train.getSeats().stream().filter(seat -> seat.getTicket() == null).findFirst().ifPresentOrElse(seat -> {
            ticketRepo.save(new Ticket().setSeat(seat));
        }, () -> {
            throw new BadRequestException("票已卖完");
        });
    }

    @GetMapping("/trains")
    public List<Train> queryTrains() {
        return trainRepo.findAll();
    }

}
