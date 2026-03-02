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

/**
 * Service for retrieving dining table availability for a requested time slot.
 *
 * This service calculates the reservation time window using ReservationPolicy.DEFAULT_DURATION,
 * finds overlapping reservations and returns per-table availability and suitability information.
 */
@Service
public class AvailabilityService {

    private final DiningTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public AvailabilityService(DiningTableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Returns availability information for all dining tables for the given date and start time.
     *
     * A table is marked as occupied if it has any reservation overlapping the requested time window.
     * A table is marked as suitable if its capacity is greater than or equal to the requested party size.
     *
     * If zone is provided, only tables in that zone are included.
     *
     * @param date reservation date
     * @param time reservation start time
     * @param partySize number of guests
     * @param zone optional zone filter; if null, all zones are included
     * @return list of availability results for each table
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