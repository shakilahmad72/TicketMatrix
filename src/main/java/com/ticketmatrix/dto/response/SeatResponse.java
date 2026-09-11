package com.ticketmatrix.dto.response;

import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;

import java.math.BigDecimal;

public record SeatResponse(
        Long seatId,
        String seatNumber,
        SeatStatus status,
        BigDecimal price
) {
    // Static mapper method directly inside the record for clean conversions
    public static SeatResponse fromEntity(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getStatus(),
                seat.getPrice()
        );
    }
}
