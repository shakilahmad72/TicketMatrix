package com.ticketmatrix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HoldSeatRequest(
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be a positive integer")
        Long userId,

        @NotNull(message = "Seat ID is required")
        @Positive(message = "Seat ID must be a positive integer")
        Long seatId
) {}
