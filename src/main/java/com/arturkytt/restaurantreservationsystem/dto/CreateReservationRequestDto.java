package com.arturkytt.restaurantreservationsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO used when creating a new reservation.
 *
 * This object represents the data sent by the client when requesting
 * to create a reservation for a specific table and time.
 *
 * tableId identifies the table to be reserved.
 * date represents the reservation date.
 * time represents the reservation start time.
 * partySize defines the number of guests included in the reservation.
 *
 * The end time is calculated in the service layer using the default reservation duration.
 */
public record CreateReservationRequestDto(
        Long tableId,
        LocalDate date,
        LocalTime time,
        int partySize
) {}