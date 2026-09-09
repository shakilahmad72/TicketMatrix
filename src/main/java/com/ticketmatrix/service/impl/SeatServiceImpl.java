package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.SeatResponse;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.SeatService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
        validateEventExits(eventId);
        return seatRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getAvailableSeatsByEventId(Long eventId) {
        validateEventExists(eventId);
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.AVILABLE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existById(eventId)) {
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
