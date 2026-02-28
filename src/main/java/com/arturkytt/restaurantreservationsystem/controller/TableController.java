package com.arturkytt.restaurantreservationsystem.controller;

import com.arturkytt.restaurantreservationsystem.dto.TableDto;
import com.arturkytt.restaurantreservationsystem.repository.DiningTableRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TableController {

    private final DiningTableRepository tableRepository;

    public TableController(DiningTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping("/tables")
    public List<TableDto> getTables() {
        return tableRepository.findAll().stream()
                .map(t -> new TableDto(
                        t.getId(),
                        t.getCode(),
                        t.getCapacity(),
                        t.getZone(),
                        t.getX(),
                        t.getY(),
                        t.getFeatures()
                ))
                .sorted(Comparator.comparing(TableDto::code))
                .toList();
    }
}