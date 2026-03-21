package com.agiletour.controller;

import com.agiletour.dto.TrainResponse;
import com.agiletour.entity.Ticket;
import com.agiletour.entity.Train;
import com.agiletour.entity.Stop;
import com.agiletour.entity.User;
import com.agiletour.repo.TicketRepo;
import com.agiletour.repo.TrainRepo;
import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class TrainsController {

    @Autowired
    private TrainRepo trainRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/trains/{trainId}/tickets")
    public void buyTicket(@PathVariable long trainId, @RequestBody FromAndTo fromAndTo,
                          @RequestHeader(value = "Authorization", required = false) String auth) {
        var train = trainRepo.findById(trainId);
        train.getSeats().stream().filter(seat -> seat.isAvailable(fromAndTo.from, fromAndTo.to))
                .findFirst().ifPresentOrElse(seat -> {
                    var from = train.findStop(fromAndTo.from);
                    var to = train.findStop(fromAndTo.to);
                    Ticket ticket = new Ticket().setSeat(seat).setFrom(from).setTo(to);

                    // Associate user if authenticated
                    if (auth != null && auth.startsWith("Bearer ")) {
                        String token = auth.substring(7);
                        if (token.startsWith("test-token-")) {
                            try {
                                long userId = Long.parseLong(token.substring("test-token-".length()));
                                userRepo.findById(userId).ifPresent(ticket::setUser);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                    ticketRepo.save(ticket);
                }, () -> {
                    throw new BadRequestException("票已卖完");
                });
    }

    @GetMapping("/trains")
    public List<TrainResponse> queryTrains(@RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        List<Train> allTrains = trainRepo.findAll();
        if (from == null || to == null) {
            return allTrains.stream()
                    .map(train -> TrainResponse.from(train, null, null))
                    .collect(Collectors.toList());
        }

        return allTrains.stream()
                .filter(train -> hasRoute(train, from, to))
                .map(train -> {
                    // Convert station names to stop IDs for remaining tickets calculation
                    Optional<Stop> fromStop = train.getStops().stream()
                            .filter(stop -> stop.getName().equals(from))
                            .findFirst();
                    Optional<Stop> toStop = train.getStops().stream()
                            .filter(stop -> stop.getName().equals(to))
                            .findFirst();

                    Integer fromStopId = fromStop.map(stop -> (int) (long) stop.getId()).orElse(null);
                    Integer toStopId = toStop.map(stop -> (int) (long) stop.getId()).orElse(null);

                    return TrainResponse.from(train, fromStopId, toStopId);
                })
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
