package com.ticketmatrix.dto.response;

import com.ticketmatrix.entity.Event;
import java.time.Instant;

public record EventResponse(
        Long id,
        String title,
        String description,
        String venue,
        Instant eventDate
) {
    public static EventResponse fromEntity(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getEventDate()
        );
    }
}
