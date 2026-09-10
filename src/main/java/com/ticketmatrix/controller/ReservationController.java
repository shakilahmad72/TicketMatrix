package com.ticketmatrix.controller;

import com.ticketmatrix.dto.request.ConfirmBookingRequest;
import com.ticketmatrix.dto.request.HoldSeatRequest;
import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.ReservationResponse;
import com.ticketmatrix.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * POST /api/v1/reservations/hold
     * Places a 10-minute temporary lock on a seat.
     * Returns 201 Created on success, or triggers 409 Conflict if already locked.
     */
    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<ReservationResponse>> holdSeat(
            @Valid @RequestBody HoldSeatRequest request) {

        ReservationResponse response = reservationService.holdSeat(request.userId(), request.seatId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seat held successfully. Complete payment within 10 minutes.", response));
    }

    /**
     * POST /api/v1/reservations/confirm
     * Finalizes booking once external payment succeeds.
     */
        @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmBooking(
                @Valid @RequestBody ConfirmBookingRequest request) {

            ReservationResponse response = reservationService.confirmBooking(
                    request.reservationToken(),
                    request.paymentReferenceId()
            );
            return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully!", response));
        }

    /**
     * DELETE /api/v1/reservations/{token}/cancel
     * Allows a user to voluntarily release their hold before the 10-minute timer expires.
     */
    @DeleteMapping("/{token}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelHold(@PathVariable String token) {
        reservationService.cancelHold(token);
        return ResponseEntity.ok(ApiResponse.success("Reservation hold cancelled and seat released."));
    }
}

