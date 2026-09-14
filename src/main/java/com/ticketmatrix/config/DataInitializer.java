package com.ticketmatrix.config;

import com.ticketmatrix.entity.Event;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataInitializer(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    public void run(String... args) {
        // Prevent duplicate seeding on restarts
        if (eventRepository.count() > 0) {
            log.info("Database already contains events. Skipping data initialization.");
            return;
        }

        log.info("Seeding initial event and seat inventory for TicketMatrix...");

        // 1. Create Sample Event
        Event event = new Event();
        event.setTitle("Coldplay: Music of the Spheres World Tour");
        event.setDescription("Live concert featuring stadium lighting and exclusive VIP zones.");
        event.setVenue("DY Patil Stadium, Mumbai");
        event.setEventDate(Instant.now().plus(30, ChronoUnit.DAYS)); // Scheduled 30 days from now

        Event savedEvent = eventRepository.save(event);
        log.info("Created Event: '{}' (ID: {})", savedEvent.getTitle(), savedEvent.getId());


    }
}
