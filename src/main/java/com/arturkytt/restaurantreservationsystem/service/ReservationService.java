package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.config.ReservationPolicy;
import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import com.arturkytt.restaurantreservationsystem.dto.CreateReservationRequestDto;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for creating reservations and enforcing core reservation rules.
 *
 * This service validates:
 * - the table exists
 * - party size does not exceed table capacity
 * - the table is not already reserved during the requested time window
 *
 * The reservation end time is derived using ReservationPolicy.DEFAULT_DURATION.
 */
@Service
public class ReservationService {

    private final DiningTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(DiningTableRepository tableRepository,
                              ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Creates a new reservation based on the provided request.
     *
     * @param request client request containing table id, start date/time and party size
     * @throws IllegalArgumentException if the table does not exist or party size exceeds capacity
     * @throws IllegalStateException if the table is already reserved for the requested time window
     */
    public void createReservation(CreateReservationRequestDto request) {

        DiningTable table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        if (request.partySize() > table.getCapacity()) {
            throw new IllegalArgumentException("Party size exceeds table capacity");
        }

        LocalDateTime start = LocalDateTime.of(request.date(), request.time());
        LocalDateTime end = start.plus(ReservationPolicy.DEFAULT_DURATION);

        List<Reservation> overlapping =
                reservationRepository.findOverlapping(start, end);

        boolean occupied = overlapping.stream()
                .anyMatch(r -> r.getTable().getId().equals(table.getId()));

        if (occupied) {
            throw new IllegalStateException("Table already reserved at that time");
        }

        Reservation reservation = new Reservation();
        reservation.setTable(table);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setPartySize(request.partySize());

        reservationRepository.save(reservation);
    }
}