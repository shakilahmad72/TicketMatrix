package com.ticketmatrix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmBookingRequest(
        @NotBlank(message = "Reservation token is required")
        String reservationToken,

        @NotBlank(message = "Payment reference ID is required")
        String paymentReferenceId
) {}

