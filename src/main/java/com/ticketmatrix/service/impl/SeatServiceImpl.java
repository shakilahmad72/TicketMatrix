package com.ticketmatrix.service.impl;

import com.ticketmatrix.dto.response.SeatResponse;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.exception.ResourceNotFoundException;
import com.ticketmatrix.repository.EventRepository;
import com.ticketmatrix.repository.SeatRepository;
import com.ticketmatrix.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    @Override
    public List<SeatResponse> getSeatsByEventId(Long eventId) {
        verifyEventExists(eventId);
        return seatRepository.findByEventId(eventId)
                .stream()
                .map(SeatResponse::fromEntity)
                .toList();
    }

    @Override
    public List<SeatResponse> getAvailableSeatsByEventId(Long eventId) {
        verifyEventExists(eventId);
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.AVAILABLE)
                .stream()
                .map(SeatResponse::fromEntity)
                .toList();
    }

    private void verifyEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with ID: " + eventId);
        }
    }
}
