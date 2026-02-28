package com.arturkytt.restaurantreservationsystem.dto;

import java.util.List;

public record RecommendationResponseDto(
        RecommendationCandidateDto recommended,
        List<RecommendationCandidateDto> topCandidates
) {}