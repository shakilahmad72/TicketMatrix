package com.ticketmatrix.repository;

import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    /**
     * 1. Read-Only Queries:
     * Fetch seats by event ID and optional status filter.
     */
    List<Seat> findByEventId(Long eventId);

    List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status);

    /**
     * 2. CRITICAL CONCURRENCY LOCK:
     * Translates to: SELECT * FROM seats WHERE id = ? FOR UPDATE
     * Other transactions trying to read/write this row will wait until the
     * holding transaction completes.
     *
     * The lock timeout (e.g. 3000ms = 3s) prevents infinite database thread blocking.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    /**
     * 3. ALTERNATIVE: Direct Atomic SQL Update
     * Great to explain during interviews!
     * Updates only if current status is AVAILABLE.
     * Returns 1 if successful, 0 if another concurrent transaction already changed it.
     */
    @Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus WHERE s.id = :seatId AND s.status = :expectedStatus")
    int updatedSeatStatusAtomic(
            @Param("seatId") Long seatId,
            @Param("expectedStatus") SeatStatus expectedStatus,
            @Param("newStatus") SeatStatus newStatus
    );

    /**
     * Checks if a seat number already exists for an event (prevents duplicates).
     */
    boolean existsByEventIdAndSeatNumber(Long eventId, String seatNumber);
}
