package com.waisl.parking.events;

import com.waisl.parking.data.MockParkingData;
import com.waisl.parking.data.ParkingZone;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class ParkingSimulator {

    private final MockParkingData data;
    private final ParkingEventPublisher publisher;
    private final Random random = new Random();

    public ParkingSimulator(MockParkingData data, ParkingEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 50_000, initialDelay = 25_000)
    public void simulateRandomEvent() {
        data.allZones().stream().findAny().ifPresent(z -> simulateSlotFreed(z.getZoneCode()));
    }

    public Map<String, Object> simulateSlotFreed(String zoneCode) {
        ParkingZone zone = data.getZone(zoneCode);
        if (zone == null) return Map.of("error", "unknown zone " + zoneCode);
        zone.freeOne();
        Map<String, Object> payload = Map.of(
                "zoneCode", zone.getZoneCode(),
                "description", zone.getDescription(),
                "availableSlots", zone.getAvailableSlots()
        );
        publisher.publish("parking.slot_freed", payload);
        return payload;
    }

    public Map<String, Object> simulatePaymentCompleted(String ticketId) {
        boolean ok = data.pay(ticketId);
        Map<String, Object> payload = Map.of("ticketId", ticketId, "paid", ok);
        publisher.publish("parking.payment_completed", payload);
        return payload;
    }
}
