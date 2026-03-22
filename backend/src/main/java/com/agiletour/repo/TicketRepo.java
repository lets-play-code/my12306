package com.agiletour.repo;

import com.agiletour.entity.Ticket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepo extends Repository<Ticket, Long> {

    List<Ticket> findAll();

    void save(Ticket ticket);

    // 新增方法：按用户查询所有票（使用 JOIN FETCH 避免 N+1）
    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH s.train " +
           "JOIN FETCH t.from " +
           "JOIN FETCH t.to " +
           "WHERE t.user.id = :userId " +
           "ORDER BY t.travelDate ASC")
    List<Ticket> findByUserIdOrderByTravelDateAsc(@Param("userId") long userId);
}
