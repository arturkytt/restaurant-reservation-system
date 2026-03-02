package com.arturkytt.restaurantreservationsystem.dto;

import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Zone;

import java.util.Set;

/**
 * DTO representing a single table candidate in the recommendation process.
 *
 * This object contains table details together with a calculated score
 * used to rank tables based on suitability and user preferences.
 *
 * score represents the computed recommendation score calculated
 * in the recommendation service.
 */
public record RecommendationCandidateDto(
        Long id,
        String code,
        int capacity,
        Zone zone,
        int x,
        int y,
        Set<Feature> features,
        int score
) {}