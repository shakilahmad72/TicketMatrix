package com.ticketmatrix;

import com.ticketmatrix.dto.response.ReservationResponse;
import com.ticketmatrix.entity.Event;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.exception.ConflictException;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


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

        for (int i = 1; i <= threadCount; i++) {
            final long userId = i; // Each thread represents a distinct user

            executor.submit(() -> {
                readyLatch.countDown(); // Announce this thread is initialized
                try {
                    startLatch.await(); // Hold execution until the signal is broadcast

                    ReservationResponse response = reservationService.holdSeat(userId, targetSeatId);
                    if (response != null && response.reservationToken() != null) {
                        successCount.incrementAndGet();
                    }
                } catch (ConflictException | PessimisticLockingFailureException e) {
                    // Valid expected outcomes for losing threads:
                    // 1. ConflictException: Acquired lock, found seat status != AVAILABLE
                    // 2. pessimisticLockingFailureException: Timed out waiting for row lock
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    unexpectedErrorCount.incrementAndGet();
                }
            });
        }

        // Wait for all 50 threads to be allocated and waiting
        readyLatch.await(10, TimeUnit.SECONDS);

        // Fire all thread simultaneously
        startLatch.countDown();

        // Allow execution to drain
        executor.shutdown();
        boolean finishedInTime = executor.awaitTermination(30, TimeUnit.SECONDS);

        // 1. Thread Pool Completion
        assertThat(finishedInTime)
                .as("All threads must finish within the timeout limit")
                .isTrue();

        // 2. Concurrency Outcome
        assertThat(successCount.get())
                .as("Exactly one user must secure the seat hold")
                .isEqualTo( 1);

        assertThat(conflictCount.get())
                .as("The other 49 threads must receive conflict or lock timeout errors")
                .isEqualTo(threadCount - 1);

        assertThat(unexpectedErrorCount.get())
                .as("No unhandled exceptions (like NPE or DB corruption) should occur")
                .isZero();

        // 3. Database State Integrity
        Seat seatInDb = seatRepository.findById(targetSeatId).orElseThrow();
        assertThat(seatInDb.getStatus())
                .as("Final seat status in DB must be HELD")
                .isEqualTo(SeatStatus.HELD);

        long reservationCount = reservationRepository.count();
        assertThat(reservationCount)
                .as("Exactly one reservation record should exists in the database")
                .isEqualTo(1);
    }
}
