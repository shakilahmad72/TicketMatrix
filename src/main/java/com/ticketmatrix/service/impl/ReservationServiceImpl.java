package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.ReservationResponse;
import com.ticketmatrix.entity.Reservation;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.ReservationStatus;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.exception.ConflictException;
import com.ticketmatrix.exception.InvalidOperationException;
import com.ticketmatrix.exception.ResourceNotFoundException;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final int HOLD_DURATION_MINUTES = 10;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Phase 1: Temporary Lock (Hold)
     * Acquires a row lock using PESSIMISTIC_WRITE.
     * Prevents duplicate holds under concurrent request spikes.
     */

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse holdSeat(Long userId, Long seatId) {

        // 1. Acquire exclusive DB lock (SELECT ... FOR UPDATE)
        Seat seat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));

        // 2. Validate current status
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new ConflictExcdption("Seat " + seat.getSeatNumber() + " is already " + seat.getStatus());
        }

        // 3. Mark seat as HELD
        seat.setStatus(SeatStatus.HELD);
        seatRepository.save(seat);

        // 4. Generate Reservation with expiration deadline
        Reservation reservation = Reservation.builder()
                .reservationToken(UUID.randomUUID().toString())
                .userId(userId)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .holdExpiresAt(Instant.now().plus(HOLD_TTL_MINUTES, ChronoUnit.MINUTES))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.fromEntity(savedReservation);
    }

    /**
     * Phase 2: Permanent Confirmation
     * Validates hold status, checks expiration, marks seat as BOOKED.
     */

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse confirmBooking(String reservationToken, String paymentReferenceId) {

        // Uses JOIN FETCH query to fetch both reservation and seat in a single query
        Reservation reservation = reservationRepository.findByReservationTokenWithSeat(reservationToken)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found for provided token"));

        // Guard: Check if already confirmed
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new ConflictException("Reservation is already confirmed.");
        }

        // Guard: Must be in PENDING status
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Cannot confirm reservation with status: " + reservation.getStatus());
        }

        // Guard: Verify hold has not expired
        if (Instant.now().isAfter(reservation.getHoldExpiresAt())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.getSeat().setStatus(SeatStatus.AVAILABLE);
            reservationRepository.save(reservation);
            throw new ConflictException("Seat hold expired. Please initiate a new hold.");
        }

        // Transition to BOOKED & CONFIRMED
        Seat seat = reservation.getStatus();
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPaymentReferenceId(paymentReferenceId);
        reservation.setConfirmedAt(Instant.now());

        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.fromEntity(saved);
    }

    /**
     * Voluntary Cancellation
     */

    public void cancelHold(String reservationToken) {
        Reservation reservation = reservationRepository.findByReservationTokenWithSeat(reservationToken)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Only pending reservation can be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Seat seat = reservation.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);

        seatRepository.save(seat);
        reservationRepository.save(reservation);
    }
}
