package com.ticketmatrix.controller;

import com.ticketmatrix.dto.request.ConfirmBookingRequest;
import com.ticketmatrix.dto.request.HoldSeatRequest;
import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // POST /api/v1/reservations/hold -> Temporarily locks seat for 10 minutes
    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<ReservationResponse>> holdSeat(
            @Valid @RequestBody HoldSeatRequest request) {

        ReservationResponse response = reservationService.holdSeat(request.userId(), request.seatId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seat held successfully. Complete payment within 10 minutes.", response));
    }

    // POST /api/v1/reservations/confirm -> Permanently confirms & books the held seat
        @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmBooking(
                @Valid @ResquestBody ConfirmBookingRequest request) {

            ReservationResponse response = reservationService.confirmBooking(
                    request.reservationToken(),
                    request.paymentReferencedId()
            );
            return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully!", response));
        }

    // DELETE /api/v1/reservations/{token}/cancel -> Voluntarily cancel hold before expiration
    @DeleteMapping("/{token}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelHold(
            @PathVariable String token) {

        reservationService.cancelHold(token);
        return ResponseEntity.ok(ApiResponse.success("Reservation hold cancelled", null));
    }
}

