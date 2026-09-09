package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.ReservationResponse;
import com.ticketmatrix.entity.Reservation;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.ReservationStatus;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final int HOLD_DURATION_MINUTES = 10;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Acquires a row lock (PESSIMISTIC_WRITE) on the seat.
     * Prevents race conditions when thousands of users target the exact same seat.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponse holdSeat(Long userId, Long seatId) {

        // 1. Acquire DB lock on the seat. Other concurrent transactions will block here.
        Seat seat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));

        // 2. State verification
        if (seat.getStatus() != SeatStatus.AVILABLE) {
            throw new ConflictException("Seat is currently " + seat.getStatus() + " and cannot be held.");
        }

        // 3. Transition seat state
        seat.setStatus(SeatStatus.HELD);
        seatRepository.save(seat);

        // 4. Generate reservation hold with expiration timestamp
        Reservation reservation = new Reservation();
        reservation.setReservationToken(UUID.randomUUID().toString());
        reservation.setUserId(userId);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setHoldExpiresAt(Instant.now().plus(HOLD_DURATION_MINUTES, ChronoUnit.MINUTES));

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToReservationResponse(savedReservation);
    }

    /**
     * Permanently commits the booking once payment completes.
     */

}
