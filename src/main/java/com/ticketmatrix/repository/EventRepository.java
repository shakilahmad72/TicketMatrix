package com.ticketmatrix.repository;

import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Fetch upcoming active events only.
     */
    List<Event> findByEventDateAfterOrderByEventDateAsc(Instant date);

    /**
     * Search events by title substring (case-insensitive).
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Event> searchByTitle(@Param("query") String query);
}
