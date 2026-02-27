package com.arturkytt.restaurantreservationsystem.repository;

import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}