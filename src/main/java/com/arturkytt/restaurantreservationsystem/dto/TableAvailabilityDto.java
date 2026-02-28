package com.arturkytt.restaurantreservationsystem.dto;

import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Zone;

import java.util.Set;

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