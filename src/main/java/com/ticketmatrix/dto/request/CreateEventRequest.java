package com.ticketmatrix.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateEventRequest (

        @NotBlank
        String title,
        String description,
        @NotBlank
        String venue,
        @NotNull @Future
        Instant eventDate
) {}
