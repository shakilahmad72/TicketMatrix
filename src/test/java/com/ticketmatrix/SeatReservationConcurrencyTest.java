package com.ticketmatrix;

import com.ticketmatrix.entity.Event;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
        event.setDescription("High concurrency locking verification");
        event.setVenue("Matrix Arena");
        event.setEventDate(Instant.now().plus(7, ChronoUnit.DAYS));
        Event savedEvent = eventRepository.save(event);

        // Create exactly ONE available seat
        Seat seat = new Seat();
        seat.setEvent(savedEvent);
        seat.setSeatNumber("VIP-CONCURRENCY-01");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setPrice(new BigDecimal("299.99"));
        Seat savedSeat = seatRepository.save(seat);

        this.targetSeatId = savedSeat.getId();
    }
    @Test
    @DisplayName("50 threads competing for the same seat: exactly 1 wins, 49 fail cleanly")
    void givenSingleAvailableSeat_when50ThreadsAttemptHoldSimultaneously_thenOnlyOneSucceeds() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        AtomicInteger unexpectedErrorCount = new AtomicInteger(0);


    }
}
