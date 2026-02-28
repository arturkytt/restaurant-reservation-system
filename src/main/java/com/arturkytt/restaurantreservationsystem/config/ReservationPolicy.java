package com.arturkytt.restaurantreservationsystem.config;

import java.time.Duration;

public final class ReservationPolicy {
    private ReservationPolicy() {}

    public static final Duration DEFAULT_DURATION = Duration.ofHours(2);
}