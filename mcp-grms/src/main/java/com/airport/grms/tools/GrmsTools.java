package com.airport.grms.tools;

import com.airport.grms.data.GateAssignment;
import com.airport.grms.data.GroundStaffShift;
import com.airport.grms.data.MockGrmsData;
import com.airport.grms.events.GrmsEventPublisher;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GrmsTools {

    private final MockGrmsData data;
    private final GrmsEventPublisher publisher;

    public GrmsTools(MockGrmsData data, GrmsEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @McpTool(description = "Get the current gate and ground equipment assignment for a flight", generateOutputSchema = false)
    public Map<String, Object> getGateAssignment(
            @McpToolParam(description = "Flight number, e.g. 6E203 (required)", required = false) String flightNumber) {
        if (flightNumber == null || flightNumber.isBlank()) {
            return Map.of("error", "flightNumber is required");
        }
        GateAssignment a = data.getAssignment(flightNumber);
        if (a == null) return Map.of("error", "No gate assignment found for flight " + flightNumber);
        return toMap(a);
    }

    @McpTool(description = "Assign ground equipment (e.g. pushback tug, baggage belt, de-icing unit) to a flight", generateOutputSchema = false)
    public Map<String, Object> assignGroundEquipment(
            @McpToolParam(description = "Flight number, e.g. 6E203 (required)", required = false) String flightNumber,
            @McpToolParam(description = "Equipment type/description to assign (required)", required = false) String equipmentType) {
        if (flightNumber == null || flightNumber.isBlank()) {
            return Map.of("error", "flightNumber is required");
        }
        if (equipmentType == null || equipmentType.isBlank()) {
            return Map.of("error", "equipmentType is required");
        }
        GateAssignment before = data.getAssignment(flightNumber);
        if (before == null) return Map.of("error", "No flight found: " + flightNumber);
        data.assignEquipment(flightNumber, equipmentType);
        Map<String, Object> payload = Map.of("flightNumber", flightNumber, "equipment", equipmentType);
        publisher.publish("grms.equipment_assigned", payload);
        return payload;
    }

    @McpTool(description = "Get available ground staff count for a shift (MORNING, AFTERNOON, NIGHT)", generateOutputSchema = false)
    public Map<String, Object> getGroundStaffAvailability(
            @McpToolParam(description = "Shift name: MORNING, AFTERNOON or NIGHT (required)", required = false) String shift) {
        if (shift == null || shift.isBlank()) {
            return Map.of("error", "shift is required");
        }
        GroundStaffShift s = data.getShift(shift);
        if (s == null) return Map.of("error", "Unknown shift " + shift);
        return Map.of("shift", s.getShift(), "totalStaff", s.getTotalStaff(), "availableStaff", s.getAvailableStaff());
    }

    @McpTool(description = "Reassign a flight to a new gate", generateOutputSchema = false)
    public Map<String, Object> reassignGate(
            @McpToolParam(description = "Flight number, e.g. 6E203 (required)", required = false) String flightNumber,
            @McpToolParam(description = "New gate code, e.g. B07 (required)", required = false) String newGate) {
        if (flightNumber == null || flightNumber.isBlank()) {
            return Map.of("error", "flightNumber is required");
        }
        if (newGate == null || newGate.isBlank()) {
            return Map.of("error", "newGate is required");
        }
        GateAssignment before = data.getAssignment(flightNumber);
        if (before == null) return Map.of("error", "No flight found: " + flightNumber);
        String oldGate = before.getGate();
        data.reassignGate(flightNumber, newGate);
        Map<String, Object> payload = Map.of("flightNumber", flightNumber, "oldGate", oldGate, "newGate", newGate);
        publisher.publish("grms.gate_reassigned", payload);
        return payload;
    }

    private Map<String, Object> toMap(GateAssignment a) {
        return Map.of(
                "flightNumber", a.getFlightNumber(),
                "gate", a.getGate(),
                "groundEquipment", a.getGroundEquipment()
        );
    }
}
