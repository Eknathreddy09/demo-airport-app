package com.waisl.digifly.events;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Manual event triggers for live demos - not part of the MCP tool surface. */
@RestController
public class DebugController {

    private final FlightSimulator simulator;

    public DebugController(FlightSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/debug/simulate-delay/{flightNumber}")
    public Map<String, Object> simulateDelay(@PathVariable String flightNumber) {
        return simulator.simulateDelay(flightNumber);
    }

    @PostMapping("/debug/simulate-gate-change/{flightNumber}")
    public Map<String, Object> simulateGateChange(@PathVariable String flightNumber) {
        return simulator.simulateGateChange(flightNumber);
    }
}
