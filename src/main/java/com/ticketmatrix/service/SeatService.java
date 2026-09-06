package com.ticketmatrix.service;

import com.ticketmatrix.dto.response.SeatResponse;
import java.util.List;

public interface SeatService {
    List<SeatResponse> getSeatsByEventId(Long, eventId);
    List<SeatResponse> getAvailableSeatsByEventId(Long, eventId);
}
