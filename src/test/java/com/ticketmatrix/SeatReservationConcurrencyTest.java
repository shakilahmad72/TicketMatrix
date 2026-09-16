package com.ticketmatrix;

import com.ticketmatrix.entity.Event;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeatReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Long targetSeatId;

    @BeforeEach
    void setUp() {
        // Clean up anu existing state
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();

        // Create an isolated event for this test
        Event event = new Event();
        event.setTitle("Stress Test Concept");

    }
}
