package com.arturkytt.restaurantreservationsystem.dto;

import java.util.List;

/**
 * Response DTO returned by the recommendation endpoint.
 *
 * recommended represents the best matching table based on scoring logic.
 * topCandidates contains a ranked list of the highest scoring tables.
 *
 * The scoring and ranking logic are implemented in the recommendation service.
 */
public record RecommendationResponseDto(
        RecommendationCandidateDto recommended,
        List<RecommendationCandidateDto> topCandidates
) {}