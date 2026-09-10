package com.ticketmatrix.controller;

import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.SeatResponse;
import com.ticketmatrix.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    // GET /api/v1/events/1/seats -> Full seating layout with current statuses
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsForEvent(
            @PathVariable Long eventId) {

        List<SeatResponse> seats = seatService.getSeatsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Seat layout retrieved successfully", seats));
    }

    // GET /api/v1/events/1/seats/available -> Filter only unreserved Seats
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(
            @PathVariable Long eventId) {

        List<SeatResponse> seats = seatService.getAvailableSeatsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Available seats retrieved", seats));
    }
}
