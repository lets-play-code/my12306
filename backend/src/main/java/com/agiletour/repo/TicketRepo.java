package com.agiletour.repo;

import com.agiletour.entity.Ticket;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface TicketRepo extends Repository<Ticket, Long> {

    List<Ticket> findAll();
}
