package com.ticketmatrix.repository;

import com.ticketmatrix.entity.Reservation;
import com.ticketmatrix.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Finds reservation by its external UUID token.
     * Uses JOIN FETCH to load the associated Seat entity in a SINGLE query,
     * completely eliminating the classic N+1 query problem.
     */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.seat WHERE r.reservationToken = :token")
    Optional<Reservation> findByReservationTokenWithSeat(@Param("token") String token);

    /**
     * Standard lookup by reservation token.
     */
    Optional<Reservation> findByReservationToken(String reservationToken);

    /**
     * Used by HoldExpirationScheduler:
     * Finds all PENDING holds that have passed their holdExpiresAt deadline.
     */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.seat " +
            "WHERE r.status = :status AND r.holdExpiresAt < :now")
    List<Reservation> findAllExpiredPending(
            @Param("status") ReservationStatus status,
            @Param("now") Instant now
    );

    /**
     * Batch expiration update:
     * Fast bulk update for high-scale cleanup without loading entities into memory.
     */
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :expireStatus " +
    "WHERE r.status = :pendingStatus AND r.holdExpiresAt < :now")
    int markAllExpiredReservation(
            @Param("pendingStatus")ReservationStatus pendingStatus,
            @Param("expiresStatus") ReservationStatus expiredStatus,
            @Param("now") Instant now
    );

}
