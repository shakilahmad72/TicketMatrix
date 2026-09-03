package com.ticketmatrix.controller;

import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.SeatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    // GET /api/v1/events/1/seats -> View all seats and their current status
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsForEvent(
            @PathVariable Long eventId) {

        List<SeatResponse> seats = seatService.getSeatsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Seats retrieved successfully", seats));
    }

    // GET /api/v1/events/1/seats/available -> View only available seats
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(
            @PathVariable Long eventId) {

        List<SeatResponse> seats = seatService.getAvailableSeatsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Available seats retrieved", seats));
    }
}
