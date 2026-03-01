package com.arturkytt.restaurantreservationsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReservationRequestDto(
        Long tableId,
        LocalDate date,
        LocalTime time,
        int partySize
) {}