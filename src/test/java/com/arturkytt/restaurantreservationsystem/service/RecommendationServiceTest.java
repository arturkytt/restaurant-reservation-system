package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.domain.*;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    private DiningTableRepository tableRepository;
    private ReservationRepository reservationRepository;
    private RecommendationService recommendationService;

    @BeforeEach
    void setup() {
        tableRepository = Mockito.mock(DiningTableRepository.class);
        reservationRepository = Mockito.mock(ReservationRepository.class);
        recommendationService = new RecommendationService(tableRepository, reservationRepository);
    }

    private DiningTable table(String code, int capacity, Zone zone, int x, int y, Set<Feature> features) {
        DiningTable t = new DiningTable();
        t.setCode(code);
        t.setCapacity(capacity);
        t.setZone(zone);
        t.setX(x);
        t.setY(y);
        t.setFeatures(features);
        return t;
    }

    @Test
    void shouldRecommendBestFittingTable() {
        // arrange
        DiningTable t1 = table("T1", 2, Zone.MAIN_HALL, 1, 1, Set.of());
        DiningTable t2 = table("T2", 4, Zone.MAIN_HALL, 2, 1, Set.of());
        t1.setId(1L);
        t2.setId(2L);

        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));
        when(reservationRepository.findOverlapping(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        // act
        var result = recommendationService.recommend(
                LocalDate.now(),
                LocalTime.of(18,0),
                2,
                null,
                Set.of()
        );

        // assert
        assertThat(result.recommended().code()).isEqualTo("T1");
    }
}