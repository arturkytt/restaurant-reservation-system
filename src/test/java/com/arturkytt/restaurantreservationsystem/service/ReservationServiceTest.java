package com.arturkytt.restaurantreservationsystem.service;

import com.arturkytt.restaurantreservationsystem.config.ReservationPolicy;
import com.arturkytt.restaurantreservationsystem.domain.DiningTable;
import com.arturkytt.restaurantreservationsystem.domain.Reservation;
import com.arturkytt.restaurantreservationsystem.domain.Zone;
import com.arturkytt.restaurantreservationsystem.dto.CreateReservationRequestDto;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Unit tests for ReservationService.
 *
 * These tests verify that the service:
 * - creates a reservation when the table is available
 * - rejects requests where party size exceeds table capacity
 * - rejects requests when the table is already reserved during the requested time window
 */
class ReservationServiceTest {

    private DiningTableRepository tableRepository;
    private ReservationRepository reservationRepository;
    private ReservationService reservationService;
    /**
     * Initializes the service with mocked repositories before each test.
     */
    @BeforeEach
    void setup() {
        tableRepository = mock(DiningTableRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        reservationService = new ReservationService(tableRepository, reservationRepository);
    }
    /**
     * Verifies that a reservation is saved when the table exists, capacity is sufficient,
     * and there are no overlapping reservations for that table.
     */
    @Test
    void shouldCreateReservationWhenTableIsFree() {
        DiningTable table = table(1L, "T1", 4, Zone.MAIN_HALL, 1, 1);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(reservationRepository.findOverlapping(any(), any())).thenReturn(List.of());

        CreateReservationRequestDto req = new CreateReservationRequestDto(
                1L, LocalDate.of(2026, 3, 1), LocalTime.of(18, 0), 4
        );

        reservationService.createReservation(req);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());

        Reservation saved = captor.getValue();
        assertThat(saved.getTable().getId()).isEqualTo(1L);
        assertThat(saved.getPartySize()).isEqualTo(4);

        LocalDateTime expectedStart = LocalDateTime.of(2026, 3, 1, 18, 0);
        assertThat(saved.getStartTime()).isEqualTo(expectedStart);
        assertThat(saved.getEndTime()).isEqualTo(expectedStart.plus(ReservationPolicy.DEFAULT_DURATION));
    }
    /**
     * Verifies that reservations are rejected when the requested party size exceeds table capacity.
     */
    @Test
    void shouldRejectWhenPartySizeExceedsCapacity() {
        DiningTable table = table(1L, "T1", 2, Zone.MAIN_HALL, 1, 1);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        CreateReservationRequestDto req = new CreateReservationRequestDto(
                1L, LocalDate.of(2026, 3, 1), LocalTime.of(18, 0), 3
        );

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reservationRepository, never()).save(any());
    }
    /**
     * Verifies that reservations are rejected when the table is already reserved
     * for the requested time window.
     */
    @Test
    void shouldRejectWhenTableIsAlreadyReserved() {
        DiningTable table = table(1L, "T1", 4, Zone.MAIN_HALL, 1, 1);

        Reservation existing = new Reservation();
        existing.setTable(table);
        existing.setStartTime(LocalDateTime.of(2026, 3, 1, 17, 0));
        existing.setEndTime(LocalDateTime.of(2026, 3, 1, 19, 0));
        existing.setPartySize(2);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(reservationRepository.findOverlapping(any(), any())).thenReturn(List.of(existing));

        CreateReservationRequestDto req = new CreateReservationRequestDto(
                1L, LocalDate.of(2026, 3, 1), LocalTime.of(18, 0), 2
        );

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(IllegalStateException.class);

        verify(reservationRepository, never()).save(any());
    }
    /**
     * Test helper for building DiningTable instances with the required attributes.
     */
    private DiningTable table(Long id, String code, int capacity, Zone zone, int x, int y) {
        DiningTable t = new DiningTable();
        t.setId(id);
        t.setCode(code);
        t.setCapacity(capacity);
        t.setZone(zone);
        t.setX(x);
        t.setY(y);
        t.setFeatures(Set.of());
        return t;
    }
}