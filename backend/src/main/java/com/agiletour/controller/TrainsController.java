package com.agiletour.controller;

import com.agiletour.entity.Ticket;
import com.agiletour.repo.TrainRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TrainsController {

    @Autowired
    private TrainRepo trainRepo;

    @PostMapping("/trains/{trainId}/tickets")
    public void buyTicket(@PathVariable long trainId) {
        var train = trainRepo.findById(trainId);
        train.getTickets().add(new Ticket().setTrain(train));
        trainRepo.save(train);
    }

}
