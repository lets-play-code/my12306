package com.agiletour.controller;

import com.agiletour.entity.Ticket;
import com.agiletour.entity.Train;
import com.agiletour.entity.Stop;
import com.agiletour.repo.TicketRepo;
import com.agiletour.repo.TrainRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class TrainsController {

    @Autowired
    private TrainRepo trainRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @PostMapping("/trains/{trainId}/tickets")
    public void buyTicket(@PathVariable long trainId, @RequestBody FromAndTo fromAndTo) {
        var train = trainRepo.findById(trainId);
        train.getSeats().stream().filter(seat -> seat.isAvailable(fromAndTo.from, fromAndTo.to))
                .findFirst().ifPresentOrElse(seat -> {
                    var from = train.findStop(fromAndTo.from);
                    var to = train.findStop(fromAndTo.to);
                    ticketRepo.save(new Ticket().setSeat(seat).setFrom(from).setTo(to));
                }, () -> {
                    throw new BadRequestException("票已卖完");
                });
    }

    @GetMapping("/trains")
    public List<Train> queryTrains(@RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        List<Train> allTrains = trainRepo.findAll();
        if (from == null || to == null) {
            return allTrains;
        }
        return allTrains.stream()
                .filter(train -> hasRoute(train, from, to))
                .collect(Collectors.toList());
    }

    private boolean hasRoute(Train train, String from, String to) {
        List<Stop> stops = train.getStops();
        boolean hasFrom = stops.stream().anyMatch(stop -> stop.getName().equals(from));
        boolean hasTo = stops.stream().anyMatch(stop -> stop.getName().equals(to));
        if (!hasFrom || !hasTo) return false;

        int fromOrder = stops.stream().filter(stop -> stop.getName().equals(from)).findFirst().get().getOrder();
        int toOrder = stops.stream().filter(stop -> stop.getName().equals(to)).findFirst().get().getOrder();
        return fromOrder < toOrder;
    }

    public static class FromAndTo {
        public int from, to;
    }
}
