package com.arturkytt.restaurantreservationsystem.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a reservation made for a specific dining table.
 *
 * This class models the time interval during which a table is reserved
 * and the number of guests included in the reservation.
 *
 * Business rules enforced in the service layer:
 * - Reservations for the same table must not overlap in time.
 * - Party size must not exceed the table capacity.
 * - Start time must be before end time.
 *
 * This entity serves as a persistence model and does not contain validation logic.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dining_table_id", nullable = false)
    private DiningTable table;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int partySize;

    public Reservation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiningTable getTable() {
        return table;
    }

    public void setTable(DiningTable table) {
        this.table = table;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }
}