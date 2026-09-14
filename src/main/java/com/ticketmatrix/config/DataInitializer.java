package com.ticketmatrix.config;

import com.ticketmatrix.entity.Event;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataInitializer(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
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

        // 2. Generate 50 Seats (5 Rows x 10 Seats per row)
        List<Seat> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};

        for (String row : rows) {
            // Rows A & B are VIP ($150.00), C-E are General Admission ($75.00)
            BigDecimal price = (row.equals("A") || row.equals("B"))
                    ? new BigDecimal("150.00")
                    : new BigDecimal("75.00");

            for (int seatNum = 1; seatNum <= 10; seatNum++) {
                Seat seat = new Seat();
                seat.setEvent(savedEvent);
                seat.setSeatNumber(row + seatNum); // e.g. "A1", "A2", ..., "E10"
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setPrice(price);
                seat.setVersion(0L);

                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        log.info("Successfully generated and persisted {} available seats for Event ID: {}", seats.size(), savedEvent.getId());
    }
}
