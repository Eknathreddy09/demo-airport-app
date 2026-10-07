package com.airport.parking.events;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Manual event triggers for live demos - not part of the MCP tool surface. */
@RestController
public class DebugController {

    private final ParkingSimulator simulator;

    public DebugController(ParkingSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/debug/simulate-slot-freed/{zoneCode}")
    public Map<String, Object> simulateSlotFreed(@PathVariable String zoneCode) {
        return simulator.simulateSlotFreed(zoneCode);
    }

    @PostMapping("/debug/simulate-payment/{ticketId}")
    public Map<String, Object> simulatePayment(@PathVariable String ticketId) {
        return simulator.simulatePaymentCompleted(ticketId);
    }
}
