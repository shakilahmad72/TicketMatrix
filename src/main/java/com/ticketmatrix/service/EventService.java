package com.ticketmatrix.service;

import com.ticketmatrix.dto.request.CreateEventRequest;
import com.ticketmatrix.dto.response.EventResponse;
import java.util.List;

public interface EventService {
    List<EventResponse> getAllUpcomingEvents();

    List<EventResponse> searchEvents(String query);

    EventResponse getEventById(Long eventId);
    EventResponse createEvent(CreateEventRequest request);
}
