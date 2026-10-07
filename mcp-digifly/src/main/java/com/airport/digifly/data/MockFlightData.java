package com.airport.digifly.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockFlightData {

    private final Map<String, FlightRecord> flights = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        LocalDate today = LocalDate.now();
        add("6E203", "IndiGo", "DEL", "BOM", FlightRecord.Direction.DEPARTURE, today.atTime(9, 15), "A12");
        add("AI441", "Air India", "DEL", "BLR", FlightRecord.Direction.DEPARTURE, today.atTime(10, 30), "B04");
        add("SG712", "SpiceJet", "DEL", "HYD", FlightRecord.Direction.DEPARTURE, today.atTime(12, 0), "A05");
        add("UK955", "Vistara", "DEL", "GOI", FlightRecord.Direction.DEPARTURE, today.atTime(14, 45), "C09");
        add("6E512", "IndiGo", "BOM", "DEL", FlightRecord.Direction.ARRIVAL, today.atTime(11, 5), "A12");
        add("AI667", "Air India", "MAA", "DEL", FlightRecord.Direction.ARRIVAL, today.atTime(13, 20), "B04");
        add("SG101", "SpiceJet", "CCU", "DEL", FlightRecord.Direction.ARRIVAL, today.atTime(15, 40), "A05");
        add("UK822", "Vistara", "PNQ", "DEL", FlightRecord.Direction.ARRIVAL, today.atTime(17, 10), "C09");
    }

    private void add(String flightNumber, String airline, String origin, String destination,
                      FlightRecord.Direction direction, LocalDateTime scheduledTime, String gate) {
        flights.put(flightNumber, new FlightRecord(flightNumber, airline, origin, destination, direction, scheduledTime, gate));
    }

    public FlightRecord get(String flightNumber) {
        return flights.get(flightNumber.toUpperCase());
    }

    public List<FlightRecord> listByDirection(FlightRecord.Direction direction) {
        return flights.values().stream()
                .filter(f -> f.getDirection() == direction)
                .sorted((a, b) -> a.getScheduledTime().compareTo(b.getScheduledTime()))
                .toList();
    }

    public Collection<FlightRecord> all() {
        return flights.values();
    }
}
