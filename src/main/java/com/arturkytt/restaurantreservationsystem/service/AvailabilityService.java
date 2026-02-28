package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.config.ReservationPolicy;
import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import com.arturkytt.restaurantreservationsystem.domain.Zone;
import com.arturkytt.restaurantreservationsystem.dto.TableAvailabilityDto;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final DiningTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public AvailabilityService(DiningTableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Returns availability information for all dining tables for a given date and time.
     * @param date the reservation date
     * @param time the reservation start time
     * @param partySize number of guests for the reservation
     * @param zone optional zone filter; if {@code null}, all zones are included
     * @return list of {@link TableAvailabilityDto}
     */
    public List<TableAvailabilityDto> getAvailability(LocalDate date, LocalTime time, int partySize, Zone zone) {
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plus(ReservationPolicy.DEFAULT_DURATION);

        // 1. Find overlapping reservations for the time window
        List<Reservation> overlapping = reservationRepository.findOverlapping(startTime, endTime);

        // 2. Collect occupied table IDs
        Set<Long> occupiedTableIds = overlapping.stream()
                .map(r -> r.getTable().getId())
                .collect(Collectors.toSet());

        // 3. Load all tables and map to DTO with status/suitable flag
        List<DiningTable> tables = tableRepository.findAll();

        return tables.stream()
                .filter(t -> zone == null || t.getZone() == zone)
                .map(t -> {
                    boolean occupied = occupiedTableIds.contains(t.getId());
                    boolean suitable = t.getCapacity() >= partySize;
                    return new TableAvailabilityDto(
                            t.getId(),
                            t.getCode(),
                            t.getCapacity(),
                            t.getZone(),
                            t.getX(),
                            t.getY(),
                            t.getFeatures(),
                            occupied,
                            suitable
                    );
                })
                .sorted(Comparator.comparing(TableAvailabilityDto::code))
                .toList();
    }
}