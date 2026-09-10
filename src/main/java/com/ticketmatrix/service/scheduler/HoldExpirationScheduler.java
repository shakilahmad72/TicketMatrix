package com.ticketmatrix.service.scheduler;

import com.ticketmatrix.entity.Reservation;
import com.ticketmatrix.entity.Seat;
import com.ticketmatrix.enums.ReservationStatus;
import com.ticketmatrix.enums.SeatStatus;
import com.ticketmatrix.repository.ReservationRepository;
import com.ticketmatrix.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class HoldExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(HoldExpirationScheduler.class);

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public HoldExpirationScheduler(ReservationRepository reservationRepository, SeatRepository seatRepository) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Executes every 30 seconds to reclaim seats with expired hold windows.
     */
    @Scheduled(fixedDelayString = "${ticketmatrix.scheduler.cleanup-interval-ms:30000}")
    @Transactional
    public void releasedExpiredHolds() {
        Instant now = Instant.now();
        List<Reservation> expiredList = reservationRepository
                .findAllByStatusAndHoldExpiresAtBefore(ReservationStatus.PENDING, now);

        if (expiredList.isEmpty()) {
            return;
        }

        log.info("Found {} expired reservation hold(s). Releasing seats...", expiredList.size());

        for (Reservation reservation : expiredList) {
            reservation.setStatus(ReservationStatus.EXPIRED);

            Seat seat = reservation.getSeat();
            seat.setStatus(SeatStatus.AVAILABLE);

            seatRepository.save(seat);
            reservationRepository.save(reservation);

            log.debug("Released seat ID: {} from expired reservation token: {}",
                    seat.getId(), reservation.getReservationToken());
        }
    }
}
