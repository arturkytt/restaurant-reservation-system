package com.arturkytt.restaurantreservationsystem.repository;

import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

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