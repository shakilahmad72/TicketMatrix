package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.request.CreateEventRequest;
import com.ticketmatrix.dto.response.EventResponse;
import com.ticketmatrix.entity.Event;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventResponse> getAllUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(Instant.now())
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

    @Override
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        return EventResponse.fromEntity(event);
    }

    @Override
    @Transactional // Writable transaction
    public EventResponse createEvent(CreateEventRequest request) {
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .venue(request.venue())
                .eventDate(request.eventDate())
                .build();
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }
}
