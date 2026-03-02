package com.arturkytt.restaurantreservationsystem.dto;

import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Zone;

import java.util.Set;

/**
 * DTO representing a dining table without availability or scoring information.
 *
 * This object is typically used for general table listing endpoints
 * where only static table information is required.
 */
public record TableDto(
        Long id,
        String code,
        int capacity,
        Zone zone,
        int x,
        int y,
        Set<Feature> features
) {}