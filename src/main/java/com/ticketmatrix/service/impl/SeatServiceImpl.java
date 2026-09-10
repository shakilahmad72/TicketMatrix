package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.SeatResponse;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.SeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // Optimizes DB connection for read-only throughout
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatServiceImpl(SeatRepository seatRepository, EventRepository eventRepository) {
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<SeatResponse> getSeatsByEventId(Long eventId) {
        validateEventExists(eventId);
        return seatRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getAvailableSeatsByEventId(Long eventId) {
        validateEventExists(eventId);
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.AVAILABLE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with ID: " + eventId);
        }
    }


    private SeatResponse mapToResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getStatus(),
                seat.getPrice()
        );
    }
}
