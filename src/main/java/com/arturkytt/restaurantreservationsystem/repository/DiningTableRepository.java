package com.arturkytt.restaurantreservationsystem.repository;

import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
}