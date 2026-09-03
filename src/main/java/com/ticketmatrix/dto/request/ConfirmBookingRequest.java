package com.ticketmatrix.dto.request;

public record ConfirmBookingRequest(
        String reservationToken,

        String paymentReferencedId
) {}

