package com.airport.grms.events;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Manual event triggers for live demos - not part of the MCP tool surface. */
@RestController
public class DebugController {

    private final GrmsSimulator simulator;

    public DebugController(GrmsSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/debug/simulate-gate-reassignment/{flightNumber}")
    public Map<String, Object> simulateGateReassignment(@PathVariable String flightNumber) {
        return simulator.simulateGateReassignment(flightNumber);
    }

    @PostMapping("/debug/simulate-equipment-assigned/{flightNumber}")
    public Map<String, Object> simulateEquipmentAssigned(@PathVariable String flightNumber,
                                                           @RequestParam String equipmentType) {
        return simulator.simulateEquipmentAssigned(flightNumber, equipmentType);
    }
}
