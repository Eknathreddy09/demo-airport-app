package com.waisl.parking.data;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingZone {
    private final String zoneCode;
    private final String description;
    private final int totalSlots;
    private final AtomicInteger occupiedSlots;
    private final double hourlyRate;

    public ParkingZone(String zoneCode, String description, int totalSlots, int occupiedSlots, double hourlyRate) {
        this.zoneCode = zoneCode;
        this.description = description;
        this.totalSlots = totalSlots;
        this.occupiedSlots = new AtomicInteger(occupiedSlots);
        this.hourlyRate = hourlyRate;
    }

    public String getZoneCode() { return zoneCode; }
    public String getDescription() { return description; }
    public int getTotalSlots() { return totalSlots; }
    public int getOccupiedSlots() { return occupiedSlots.get(); }
    public int getAvailableSlots() { return totalSlots - occupiedSlots.get(); }
    public double getHourlyRate() { return hourlyRate; }

    public boolean occupyOne() {
        return occupiedSlots.updateAndGet(v -> v < totalSlots ? v + 1 : v) <= totalSlots
                && occupiedSlots.get() > 0;
    }

    public void freeOne() {
        occupiedSlots.updateAndGet(v -> v > 0 ? v - 1 : v);
    }
}
