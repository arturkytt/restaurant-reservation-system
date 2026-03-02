package com.arturkytt.restaurantreservationsystem.controller;

import com.arturkytt.restaurantreservationsystem.dto.CreateReservationRequestDto;
import com.arturkytt.restaurantreservationsystem.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * REST controller responsible for creating reservations.
 *
 * Provides an endpoint for submitting new reservation requests.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Creates a new reservation.
     *
     * @param request reservation request payload
     */
    @PostMapping
    public void create(@RequestBody CreateReservationRequestDto request) {
        reservationService.createReservation(request);
    }
}