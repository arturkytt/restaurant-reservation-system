package com.arturkytt.restaurantreservationsystem.repository;

import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for accessing and managing Reservation entities.
 *
 * In addition to standard CRUD operations provided by Spring Data JPA,
 * this repository defines a custom query for detecting overlapping reservations.
 *
 * Overlap logic is based on time interval intersection:
 * A reservation overlaps if its start time is before the requested end time
 * and its end time is after the requested start time.
 *
 * This repository does not enforce business rules itself.
 * It only provides the data required for validation in the service layer.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Finds all reservations that overlap with the given time interval.
     *
     * Overlap condition:
     * existing.startTime < requestedEnd
     * AND
     * existing.endTime   > requestedStart
     *
     * @param startTime start of the requested interval (inclusive)
     * @param endTime   end of the requested interval (exclusive)
     * @return list of reservations that intersect with the given time interval
     */
    @Query("""
        select r
        from Reservation r
        where r.startTime < :endTime
          and r.endTime   > :startTime
    """)
    List<Reservation> findOverlapping(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}