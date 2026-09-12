package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.request.CreateEventRequest;
import com.ticketmatrix.dto.response.EventResponse;
import com.ticketmatrix.entity.Event;
import com.ticketmatrix.exception.ResourceNotFoundException;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventResponse> getAllUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(Instant.now())
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

    @Override
    public List<EventResponse> searchEvents(String query) {
        return eventRepository.searchByTitle(query)
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
    public EventResponse createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setEventDate(request.eventDate());

        Event savedEvent = eventRepository.save(event);
        return EventResponse.fromEntity(savedEvent);
    }
}
