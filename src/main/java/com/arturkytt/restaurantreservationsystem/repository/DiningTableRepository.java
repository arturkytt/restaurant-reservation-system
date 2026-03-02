package com.arturkytt.restaurantreservationsystem.repository;

import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing DiningTable entities.
 *
 * This repository provides basic CRUD operations through Spring Data JPA.
 * It is responsible only for data access and does not contain business logic.
 *
 * Custom filtering, availability checks and recommendation logic are implemented
 * in the service layer.
 */
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
}