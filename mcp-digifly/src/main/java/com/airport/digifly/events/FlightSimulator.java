package com.airport.digifly.events;

import com.airport.digifly.data.FlightRecord;
import com.airport.digifly.data.MockFlightData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Periodically mutates a random flight to give the demo a live, event-driven feel.
 * Also drives the /debug endpoints so a presenter can trigger an event on cue.
 */
@Component
public class FlightSimulator {

    private final MockFlightData data;
    private final FlightEventPublisher publisher;
    private final Random random = new Random();

    public FlightSimulator(MockFlightData data, FlightEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 45_000, initialDelay = 20_000)
    public void simulateRandomEvent() {
        List<FlightRecord> flights = data.all().stream().toList();
        if (flights.isEmpty()) return;
        FlightRecord flight = flights.get(random.nextInt(flights.size()));
        if (random.nextBoolean()) {
            simulateDelay(flight.getFlightNumber());
        } else {
            simulateGateChange(flight.getFlightNumber());
        }
    }

    public Map<String, Object> simulateDelay(String flightNumber) {
        FlightRecord flight = data.get(flightNumber);
        if (flight == null) return Map.of("error", "unknown flight " + flightNumber);
        int delay = 15 + random.nextInt(46);
        flight.setStatus(FlightRecord.Status.DELAYED);
        flight.setDelayMinutes(delay);
        Map<String, Object> payload = Map.of(
                "flightNumber", flight.getFlightNumber(),
                "airline", flight.getAirline(),
                "delayMinutes", delay,
                "status", flight.getStatus().name()
        );
        publisher.publish("flight.delayed", payload);
        return payload;
    }

    public Map<String, Object> simulateGateChange(String flightNumber) {
        FlightRecord flight = data.get(flightNumber);
        if (flight == null) return Map.of("error", "unknown flight " + flightNumber);
        String newGate = randomGate();
        String oldGate = flight.getGate();
        flight.setGate(newGate);
        Map<String, Object> payload = Map.of(
                "flightNumber", flight.getFlightNumber(),
                "airline", flight.getAirline(),
                "oldGate", oldGate,
                "newGate", newGate
        );
        publisher.publish("flight.gate_changed", payload);
        return payload;
    }

    private String randomGate() {
        char pier = "ABC".charAt(random.nextInt(3));
        int number = 1 + random.nextInt(15);
        return pier + String.format("%02d", number);
    }
}
