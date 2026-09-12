package com.ticketmatrix.controller;

import com.ticketmatrix.dto.request.CreateEventRequest;
import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.EventResponse;
import com.ticketmatrix.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 1. GET /api/v1/events -> List all active upcoming events
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllActiveEvents() {
        List<EventResponse> events = eventService.getAllUpcomingEvents();
        return ResponseEntity.ok(ApiResponse.success("Active events retrieved successfully", events));
    }

    // 2. GET /api/v1/events/{id} -> Get specific event details
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Event details retrieved", event));
    }

    // 3. GET /api/v1/events/search?query=Coldplay
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EventResponse>>> searchEvents(@RequestParam String query) {
        List<EventResponse> events = eventService.searchEvents(query);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", events));
    }

    // 4. POST /api/v1/events -> CREATE EVENT (Fixes the "unused" warning!)
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        EventResponse createdEvent = eventService.createEvent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Event created successfully", createdEvent));
    }
}
