package com.airport.grms.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockGrmsData {

    private final Map<String, GateAssignment> gateAssignments = new ConcurrentHashMap<>();
    private final Map<String, GroundStaffShift> staffShifts = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        gateAssignments.put("6E203", new GateAssignment("6E203", "A12"));
        gateAssignments.put("AI441", new GateAssignment("AI441", "B04"));
        gateAssignments.put("SG712", new GateAssignment("SG712", "A05"));
        gateAssignments.put("UK955", new GateAssignment("UK955", "C09"));
        gateAssignments.get("6E203").setGroundEquipment("pushback-tug-3, baggage-belt-2");
        gateAssignments.get("AI441").setGroundEquipment("pushback-tug-1");

        staffShifts.put("MORNING", new GroundStaffShift("MORNING", 40, 6));
        staffShifts.put("AFTERNOON", new GroundStaffShift("AFTERNOON", 35, 11));
        staffShifts.put("NIGHT", new GroundStaffShift("NIGHT", 20, 14));
    }

    public GateAssignment getAssignment(String flightNumber) {
        return gateAssignments.get(flightNumber.toUpperCase());
    }

    public List<GateAssignment> all() {
        return List.copyOf(gateAssignments.values());
    }

    public GroundStaffShift getShift(String shift) {
        return staffShifts.get(shift.toUpperCase());
    }

    public void assignEquipment(String flightNumber, String equipmentType) {
        GateAssignment a = getAssignment(flightNumber);
        if (a != null) a.setGroundEquipment(equipmentType);
    }

    public void reassignGate(String flightNumber, String newGate) {
        GateAssignment a = getAssignment(flightNumber);
        if (a != null) a.setGate(newGate);
    }
}
