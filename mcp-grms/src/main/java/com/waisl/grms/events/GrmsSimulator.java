package com.waisl.grms.events;

import com.waisl.grms.data.GateAssignment;
import com.waisl.grms.data.MockGrmsData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class GrmsSimulator {

    private final MockGrmsData data;
    private final GrmsEventPublisher publisher;
    private final Random random = new Random();

    public GrmsSimulator(MockGrmsData data, GrmsEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 55_000, initialDelay = 30_000)
    public void simulateRandomEvent() {
        List<GateAssignment> all = data.all();
        if (all.isEmpty()) return;
        GateAssignment a = all.get(random.nextInt(all.size()));
        simulateGateReassignment(a.getFlightNumber());
    }

    public Map<String, Object> simulateGateReassignment(String flightNumber) {
        GateAssignment a = data.getAssignment(flightNumber);
        if (a == null) return Map.of("error", "unknown flight " + flightNumber);
        String oldGate = a.getGate();
        String newGate = randomGate();
        data.reassignGate(flightNumber, newGate);
        Map<String, Object> payload = Map.of(
                "flightNumber", flightNumber,
                "oldGate", oldGate,
                "newGate", newGate
        );
        publisher.publish("grms.gate_reassigned", payload);
        return payload;
    }

    public Map<String, Object> simulateEquipmentAssigned(String flightNumber, String equipmentType) {
        data.assignEquipment(flightNumber, equipmentType);
        Map<String, Object> payload = Map.of("flightNumber", flightNumber, "equipment", equipmentType);
        publisher.publish("grms.equipment_assigned", payload);
        return payload;
    }

    private String randomGate() {
        char pier = "ABC".charAt(random.nextInt(3));
        int number = 1 + random.nextInt(15);
        return pier + String.format("%02d", number);
    }
}
