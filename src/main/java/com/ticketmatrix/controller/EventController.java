package com.ticketmatrix.controller;

import com.ticketmatrix.dto.response.ApiResponse;
import com.ticketmatrix.dto.response.EventResponse;
import com.ticketmatrix.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // GET /api/v1/events -> List all active upcoming events
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventReponse>>> getAllActiveEvents() {
        List<EventResponse> events = eventService.getAllUpcomingEvents();
        return ResponseEntity.ok(ApiResponse.success("Active events retrieved successfully", events));
    }

    // GET /api/v1/events/{id} -> Get specific event details
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Event details retrieved", event));
    }
}
