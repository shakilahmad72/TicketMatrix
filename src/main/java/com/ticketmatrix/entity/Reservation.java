package com.ticketmatrix.entity;

import com.ticketmatrix.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reservations",
        indexes = {
                // Optimizes hold lookup by UUID token
                @Index(name = "idx_res_token", columnList = "reservation_token", unique = true),
                // Optimizes background worker scanning: WHERE status = 'pending' and hold_expires_at < NOW()
                @Index(name = "idx_res_cleanup", columnList = "status, hold_expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Column(name = "reservation_token", nullable = flase, unique = true, length = 64)
    public String reservationToken; // UUID sent to client

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Reservation status = ReservationStatus.PENDING;
}
