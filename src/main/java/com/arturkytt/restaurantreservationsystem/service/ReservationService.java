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

@Service
public class ReservationService {

    private final DiningTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(DiningTableRepository tableRepository,
                              ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

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