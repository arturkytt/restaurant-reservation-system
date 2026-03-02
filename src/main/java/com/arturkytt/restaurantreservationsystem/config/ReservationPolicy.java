package com.arturkytt.restaurantreservationsystem.config;

import java.time.Duration;

/**
 * Central place for reservation-related defaults used across the application.
 *
 * This class defines configuration constants that represent business defaults,
 * for example the default reservation duration
 */
public final class ReservationPolicy {
    private ReservationPolicy() {}

    /**
     * Default duration used for a reservation.
     */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(2);
}