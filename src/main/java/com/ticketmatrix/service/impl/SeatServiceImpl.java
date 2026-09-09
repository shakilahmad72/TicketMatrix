package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.SeatResponse;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.SeatService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
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

    private SeatResponse mapToResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getStatus(),
                seat.getPrice()
        );
    }
}
