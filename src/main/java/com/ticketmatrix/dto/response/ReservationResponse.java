package com.ticketmatrix.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketmatrix.entity.Reservation;
import com.ticketmatrix.enums.ReservationStatus;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservationResponse(
        Long reservationId,
        String reservationToken,
        Long userId,
        Long seatId,
        String seatNumber,
        ReservationStatus status,
        Instant holdExpiresAt,
        String paymentReferenceId,
        Instant confirmedAt
) {
    public static ReservationResponse fromEntity(Reservation res) {
        return new ReservationResponse(
                res.getId(),
                res.getReservationToken(),
                res.getUserId(),
                res.getSeat().getId(),
                res.getSeat().getSeatNumber(),
                res.getStatus(),
                res.getHoldExpiresAt(),
                res.getPaymentReferenceId(),
                res.getConfirmedAt()
        );
    }
}
