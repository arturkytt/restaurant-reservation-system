package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.config.ReservationPolicy;
import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import com.arturkytt.restaurantreservationsystem.domain.Feature;
import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import com.arturkytt.restaurantreservationsystem.domain.Zone;
import com.arturkytt.restaurantreservationsystem.dto.RecommendationCandidateDto;
import com.arturkytt.restaurantreservationsystem.dto.RecommendationResponseDto;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that recommends the best available table based on availability and preferences.
 *
 * The service:
 * - excludes occupied tables based on overlapping reservations in the requested time window
 * - excludes tables with insufficient capacity
 * - optionally filters by zone
 * - scores the remaining candidates and selects the highest scoring table
 *
 * If no candidates are available, the recommended table is null.
 */
@Service
public class RecommendationService {

    private final DiningTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public RecommendationService(DiningTableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Returns a recommendation result for the given time slot and requirements.
     *
     * @param date reservation date
     * @param time reservation start time
     * @param partySize number of guests
     * @param zone optional zone restriction; if null, all zones are considered
     * @param requestedFeatures optional feature preferences used in scoring
     * @return recommendation response containing the best table (or null) and the top candidates
     */
    public RecommendationResponseDto recommend(
            LocalDate date,
            LocalTime time,
            int partySize,
            Zone zone,
            Set<Feature> requestedFeatures
    ) {
        // Assumption: default reservation duration is fixed (2 hours).
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plus(ReservationPolicy.DEFAULT_DURATION);

        // Find reservations that overlap with the requested window.
        List<Reservation> overlapping = reservationRepository.findOverlapping(startTime, endTime);

        // Mark occupied tables (O(1) lookup later).
        Set<Long> occupiedTableIds = overlapping.stream()
                .map(r -> r.getTable().getId())
                .collect(Collectors.toSet());

        // Fetch all tables once; then filter in memory.
        List<DiningTable> tables = tableRepository.findAll();

        // Filter by availability, capacity and optional zone.
        List<DiningTable> candidates = tables.stream()
                .filter(t -> !occupiedTableIds.contains(t.getId()))
                .filter(t -> t.getCapacity() >= partySize)
                .filter(t -> zone == null || t.getZone() == zone)
                .toList();

        // Score and sort candidates: highest score wins.
        List<RecommendationCandidateDto> scored = candidates.stream()
                .map(t -> toScoredDto(t, partySize, zone, requestedFeatures))
                .sorted(Comparator.comparingInt(RecommendationCandidateDto::score).reversed()
                        .thenComparing(RecommendationCandidateDto::code))
                .toList();

        RecommendationCandidateDto best = scored.isEmpty() ? null : scored.getFirst();
        List<RecommendationCandidateDto> top = scored.stream().limit(3).toList();

        return new RecommendationResponseDto(best, top);
    }

    /**
     * Computes a recommendation score for a single candidate table.
     *
     * Scoring rules:
     * - capacity fit: prefer minimal unused seats
     * - zone bonus when a zone is requested and matches
     * - feature match bonus for each requested feature present on the table
     *
     * @param t candidate table
     * @param partySize requested number of guests
     * @param requestedZone requested zone (may be null)
     * @param requestedFeatures requested features (may be null or empty)
     * @return DTO containing table details and the calculated score
     */
    private RecommendationCandidateDto toScoredDto(DiningTable t, int partySize, Zone requestedZone, Set<Feature> requestedFeatures) {
        int score = 0;

        // Capacity fit: smaller waste is better.
        // Example: partySize=4, capacity=4 => waste=0 => max points here
        int waste = t.getCapacity() - partySize;
        score += Math.max(0, 20 - waste * 2);

        // Zone match bonus (if zone is requested).
        if (requestedZone != null && t.getZone() == requestedZone) {
            score += 5;
        }

        // Feature match bonus.
        if (requestedFeatures != null && !requestedFeatures.isEmpty()) {
            int matches = 0;
            for (Feature f : requestedFeatures) {
                if (t.getFeatures().contains(f)) {
                    matches++;
                }
            }
            score += matches * 10;
        }

        return new RecommendationCandidateDto(
                t.getId(),
                t.getCode(),
                t.getCapacity(),
                t.getZone(),
                t.getX(),
                t.getY(),
                t.getFeatures(),
                score
        );
    }
}