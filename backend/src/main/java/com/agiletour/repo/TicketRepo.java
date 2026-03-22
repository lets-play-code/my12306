package com.agiletour.repo;

import com.agiletour.entity.Ticket;
import com.agiletour.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepo extends Repository<Ticket, Long> {

    List<Ticket> findAll();

    void save(Ticket ticket);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.from f JOIN FETCH t.to JOIN FETCH t.seat s JOIN FETCH s.train JOIN FETCH t.user WHERE t.user = :user ORDER BY f.departureTime ASC")
    List<Ticket> findByUserOrderByFromDepartureTime(@Param("user") User user);
}
