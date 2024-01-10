package com.agiletour.controller;

import com.agiletour.entity.Seat;
import com.agiletour.entity.Stop;
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

    private static boolean isAvailable(Seat seat) {
        List<Seat> seats = seat.getTrain().getSeats();
        seats.forEach(s -> {

        });

        return seat.getTickets().isEmpty() || !getLastStop(seat).getName().equals(seat.getTickets().get(0).getTo().getName());
    }

    private static Stop getLastStop(Seat seat) {
        return seat.getTrain().getStops().get(seat.getTrain().getStops().size() - 1);
    }

    @PostMapping("/trains/{trainId}/tickets")
    public void buyTicket(@PathVariable long trainId, @RequestBody FromAndTo fromAndTo) {
        var train = trainRepo.findById(trainId);
        train.getSeats().stream().filter(seat -> isAvailable(seat)).findFirst().ifPresentOrElse(seat -> {
            var from = train.findStop(fromAndTo.from);
            var to = train.findStop(fromAndTo.to);
            ticketRepo.save(new Ticket().setSeat(seat).setFrom(from).setTo(to));
        }, () -> {
            throw new BadRequestException("票已卖完");
        });
    }

    @GetMapping("/trains")
    public List<Train> queryTrains() {
        return trainRepo.findAll();
    }

    public static class FromAndTo {
        public int from, to;
    }
}
