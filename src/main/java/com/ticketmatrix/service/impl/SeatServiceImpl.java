package com.ticketmatrix.service.impl;

import com.ticketmatrix.service.SeatService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatServiceImpl()
    }

}
