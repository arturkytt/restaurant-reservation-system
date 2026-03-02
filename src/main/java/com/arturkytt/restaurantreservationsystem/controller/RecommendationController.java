package com.arturkytt.restaurantreservationsystem.controller;

import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Zone;
import com.arturkytt.restaurantreservationsystem.dto.RecommendationResponseDto;
import com.arturkytt.restaurantreservationsystem.service.RecommendationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * REST controller providing table recommendation endpoints.
 *
 * Exposes an endpoint that returns the best available table
 * based on time, party size and optional preferences.
 */
@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    /**
     * Returns a recommendation result for the given criteria.
     *
     * @param date reservation date
     * @param time reservation start time
     * @param partySize number of guests
     * @param zone optional zone restriction
     * @param features optional comma-separated list of requested features
     * @return recommendation response containing the best table and top candidates
     */
    @GetMapping("/recommendation")
    public RecommendationResponseDto recommend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            @RequestParam int partySize,
            @RequestParam(required = false) Zone zone,
            @RequestParam(required = false) String features
    ) {
        Set<Feature> requested = parseFeatures(features);
        return recommendationService.recommend(date, time, partySize, zone, requested);
    }
    /**
     * Parses a comma-separated feature list into a set of Feature enums.
     *
     * @param features comma-separated feature names
     * @return parsed feature set, or an empty set if none provided
     */
    private Set<Feature> parseFeatures(String features) {
        if (features == null || features.isBlank()) {
            return EnumSet.noneOf(Feature.class);
        }
        EnumSet<Feature> set = EnumSet.noneOf(Feature.class);
        Arrays.stream(features.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(Feature::valueOf)
                .forEach(set::add);
        return set;
    }
}