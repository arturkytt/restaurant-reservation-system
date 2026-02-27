package com.arturkytt.restaurantreservationsystem.config;

import com.arturkytt.restaurantreservationsystem.domain.*;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import com.arturkytt.restaurantreservationsystem.repository.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/*
Reservations are generated deterministically using Random(42)
Assumption: each reservation lasts 2 hours
*/
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(DiningTableRepository tableRepo, ReservationRepository reservationRepo) {
        return args -> {
            if (tableRepo.count() > 0) {
                return; // already seeded
            }

            // 1. Create tables
            List<DiningTable> tables = List.of(
                    createTable("T1", 2, Zone.MAIN_HALL, 2, 2, EnumSet.of(Feature.QUIET)),
                    createTable("T2", 2, Zone.MAIN_HALL, 4, 2, EnumSet.of(Feature.WINDOW)),
                    createTable("T3", 4, Zone.MAIN_HALL, 6, 2, EnumSet.of(Feature.WINDOW)),
                    createTable("T4", 4, Zone.MAIN_HALL, 2, 4, EnumSet.of(Feature.ACCESSIBLE)),
                    createTable("T5", 6, Zone.MAIN_HALL, 4, 4, EnumSet.of(Feature.KIDS_NEAR)),

                    createTable("T6", 2, Zone.TERRACE, 2, 6, EnumSet.of(Feature.WINDOW)),
                    createTable("T7", 4, Zone.TERRACE, 4, 6, EnumSet.of(Feature.WINDOW, Feature.QUIET)),
                    createTable("T8", 6, Zone.TERRACE, 6, 6, EnumSet.noneOf(Feature.class)),

                    createTable("P1", 4, Zone.PRIVATE_ROOM, 9, 2, EnumSet.of(Feature.QUIET)),
                    createTable("P2", 8, Zone.PRIVATE_ROOM, 9, 4, EnumSet.of(Feature.QUIET, Feature.ACCESSIBLE))
            );

            tableRepo.saveAll(tables);

            // 2. Random reservations
            Random rnd = new Random(42);
            LocalDate startDate = LocalDate.now();
            int days = 7;

            for (int d = 0; d < days; d++) {
                LocalDate date = startDate.plusDays(d);

                for (DiningTable table : tables) {
                    // 0-2 reservations per table per day
                    int count = rnd.nextInt(3);

                    for (int i = 0; i < count; i++) {
                        LocalTime start = LocalTime.of(12 + rnd.nextInt(8), 0); // 12:00..19:00
                        LocalDateTime startTime = LocalDateTime.of(date, start);
                        LocalDateTime endTime = startTime.plusHours(2);

                        int partySize = Math.min(table.getCapacity(), 1 + rnd.nextInt(table.getCapacity()));

                        Reservation r = new Reservation();
                        r.setTable(table);
                        r.setStartTime(startTime);
                        r.setEndTime(endTime);
                        r.setPartySize(partySize);

                        reservationRepo.save(r);
                    }
                }
            }
        };
    }


    private DiningTable createTable(String code, int capacity, Zone zone, int x, int y, EnumSet<Feature> features) {
        DiningTable t = new DiningTable();
        t.setCode(code);
        t.setCapacity(capacity);
        t.setZone(zone);
        t.setX(x);
        t.setY(y);
        t.setFeatures(features);
        return t;
    }
}