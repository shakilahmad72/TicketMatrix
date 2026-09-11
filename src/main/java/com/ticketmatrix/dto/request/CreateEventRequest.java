package com.ticketmatrix.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateEventRequest (

        @NotBlank(message = "Title cannot be blank")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotBlank(message = "Venue cannot be blank")
        String venue,

        @NotNull(message = "Event date cannot be null")
        @Future(message = "Event date must be in the future")
        Instant eventDate
) {}
