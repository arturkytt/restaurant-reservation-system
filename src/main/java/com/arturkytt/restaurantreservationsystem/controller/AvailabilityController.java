package com.arturkytt.restaurantreservationsystem.controller;

import com.arturkytt.restaurantreservationsystem.domain.Zone;
import com.arturkytt.restaurantreservationsystem.dto.TableAvailabilityDto;
import com.arturkytt.restaurantreservationsystem.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST controller providing table availability endpoints.
 *
 * Exposes an endpoint for retrieving availability and suitability
 * information for all tables for a specific date and time.
 */
@RestController
@RequestMapping("/api")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Returns availability information for all tables for the given time slot.
     *
     * @param date reservation date
     * @param time reservation start time
     * @param partySize number of guests
     * @param zone optional zone filter; if not provided, all zones are included
     * @return list of table availability results
     */
    @GetMapping("/availability")
    public List<TableAvailabilityDto> availability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            @RequestParam int partySize,
            @RequestParam(required = false) Zone zone
    ) {
        return availabilityService.getAvailability(date, time, partySize, zone);
    }
}