package com.waisl.parking.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MockParkingData {

    private final Map<String, ParkingZone> zones = new ConcurrentHashMap<>();
    private final Map<String, ParkingTicket> tickets = new ConcurrentHashMap<>();
    private final AtomicInteger ticketSequence = new AtomicInteger(1000);

    @PostConstruct
    void seed() {
        zones.put("T3-P1", new ParkingZone("T3-P1", "Terminal 3 - Short Term", 200, 165, 80));
        zones.put("T3-P2", new ParkingZone("T3-P2", "Terminal 3 - Long Term", 500, 310, 40));
        zones.put("T1-P1", new ParkingZone("T1-P1", "Terminal 1 - Short Term", 120, 118, 70));
        zones.put("VIP-P1", new ParkingZone("VIP-P1", "VIP / Premium", 30, 12, 200));
    }

    public ParkingZone getZone(String zoneCode) {
        return zones.get(zoneCode.toUpperCase());
    }

    public Collection<ParkingZone> allZones() {
        return zones.values();
    }

    public ParkingTicket book(String zoneCode, String vehicleNumber, int durationHours) {
        ParkingZone zone = getZone(zoneCode);
        if (zone == null || zone.getAvailableSlots() <= 0) {
            return null;
        }
        zone.occupyOne();
        String ticketId = "PK" + ticketSequence.incrementAndGet();
        double amount = zone.getHourlyRate() * durationHours;
        ParkingTicket ticket = new ParkingTicket(ticketId, zone.getZoneCode(), vehicleNumber,
                LocalDateTime.now(), durationHours, amount);
        tickets.put(ticketId, ticket);
        return ticket;
    }

    public ParkingTicket getTicket(String ticketId) {
        return tickets.get(ticketId.toUpperCase());
    }

    public boolean pay(String ticketId) {
        ParkingTicket ticket = getTicket(ticketId);
        if (ticket == null) return false;
        ticket.setPaymentStatus(ParkingTicket.PaymentStatus.PAID);
        return true;
    }

    public void freeRandomSlot() {
        zones.values().stream().findAny().ifPresent(ParkingZone::freeOne);
    }
}
