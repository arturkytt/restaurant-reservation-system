package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.domain.*;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private DiningTableRepository tableRepository;
    private ReservationRepository reservationRepository;
    private RecommendationService recommendationService;

    @BeforeEach
    void setup() {
        tableRepository = mock(DiningTableRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        recommendationService = new RecommendationService(tableRepository, reservationRepository);
    }

    @Test
    void shouldRecommendBestCapacityFit() {
        DiningTable t1 = table(1L, "T1", 2, Zone.MAIN_HALL, 1, 1, Set.of());
        DiningTable t2 = table(2L, "T2", 4, Zone.MAIN_HALL, 2, 1, Set.of());

        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));
        when(reservationRepository.findOverlapping(any(), any())).thenReturn(List.of());

        var resp = recommendationService.recommend(
                LocalDate.of(2026, 3, 1),
                LocalTime.of(18, 0),
                2,
                null,
                Set.of()
        );

        assertThat(resp.recommended()).isNotNull();
        assertThat(resp.recommended().code()).isEqualTo("T1");
    }

    @Test
    void shouldIgnoreOccupiedTables() {
        DiningTable t1 = table(1L, "T1", 2, Zone.MAIN_HALL, 1, 1, Set.of());
        DiningTable t2 = table(2L, "T2", 2, Zone.MAIN_HALL, 2, 1, Set.of());

        Reservation existing = new Reservation();
        existing.setTable(t1);
        existing.setStartTime(LocalDateTime.of(2026, 3, 1, 18, 0));
        existing.setEndTime(LocalDateTime.of(2026, 3, 1, 20, 0));
        existing.setPartySize(2);

        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));
        when(reservationRepository.findOverlapping(any(), any())).thenReturn(List.of(existing));

        var resp = recommendationService.recommend(
                LocalDate.of(2026, 3, 1),
                LocalTime.of(18, 0),
                2,
                null,
                Set.of()
        );

        assertThat(resp.recommended()).isNotNull();
        assertThat(resp.recommended().code()).isEqualTo("T2");
    }

    @Test
    void shouldPreferFeatureMatch() {
        DiningTable w = table(1L, "W1", 4, Zone.MAIN_HALL, 1, 1, Set.of(Feature.WINDOW));
        DiningTable q = table(2L, "Q1", 4, Zone.MAIN_HALL, 2, 1, Set.of(Feature.QUIET));

        when(tableRepository.findAll()).thenReturn(List.of(w, q));
        when(reservationRepository.findOverlapping(any(), any())).thenReturn(List.of());

        var resp = recommendationService.recommend(
                LocalDate.of(2026, 3, 1),
                LocalTime.of(18, 0),
                4,
                Zone.MAIN_HALL,
                Set.of(Feature.WINDOW)
        );

        assertThat(resp.recommended()).isNotNull();
        assertThat(resp.recommended().code()).isEqualTo("W1");
    }

    private DiningTable table(Long id, String code, int capacity, Zone zone, int x, int y, Set<Feature> features) {
        DiningTable t = new DiningTable();
        t.setId(id);
        t.setCode(code);
        t.setCapacity(capacity);
        t.setZone(zone);
        t.setX(x);
        t.setY(y);
        t.setFeatures(features);
        return t;
    }
}