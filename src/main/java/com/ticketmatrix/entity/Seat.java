package com.ticketmatrix.entity;

import com.ticketmatrix.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                // Enforces that seat "A1" cannot be duplicated for the same event
                @UniqueConstraint(name = "uk_event_seat_number", columnNames = {"event_id", "seat_number"})
        },
        indexes = {
                // Optimizes: SELECT * FROM seats WHERE event_id = ? AND status = 'AVAILABLE'
                @Index(name = "idx_seats_event_status", columnList = "event_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber; // e.g., "A12", "VIP-01"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVILABLE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Optimistic Locking Mechanism:
     * If two transactions read version=0 at the same time,
     * the first one to commit increments version to 1.
     * The second commit will detect version mismatch and throw an OptimisticLockException.
     */
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version =0L;
}
