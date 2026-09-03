package com.ticketmatrix.dto.request;

public record HoldSeatRequest(
        Long userId,

        Long seatId
) {}
