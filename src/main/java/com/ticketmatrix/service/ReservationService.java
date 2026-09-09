package com.ticketmatrix.service;

import com.ticketmatrix.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse holdSeat(Long userId, Long SeatId);
    ReservationResponse confirmBooking(String reservationToken, String paymentReferenceId);
    void cancelHold(String reservationToken);
}
