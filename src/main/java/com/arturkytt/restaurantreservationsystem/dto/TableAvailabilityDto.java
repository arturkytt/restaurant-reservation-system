package com.arturkytt.restaurantreservationsystem.dto;

import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Zone;

import java.util.Set;

/**
 * DTO representing table availability information for a specific time interval.
 *
 * occupied indicates whether the table is already reserved during the requested interval.
 * suitable indicates whether the table meets the requested party size or other constraints.
 *
 * This DTO is typically returned by availability-related endpoints.
 */
public record TableAvailabilityDto(
        Long id,
        String code,
        int capacity,
        Zone zone,
        int x,
        int y,
        Set<Feature> features,
        boolean occupied,
        boolean suitable
) {}