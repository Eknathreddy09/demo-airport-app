package com.airport.digifly.tools;

import com.airport.digifly.data.FlightRecord;
import com.airport.digifly.data.MockFlightData;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FlightTools {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final MockFlightData data;

    public FlightTools(MockFlightData data) {
        this.data = data;
    }

    @McpTool(description = "Get the current status, gate and any delay for a specific flight number (e.g. 6E203)", generateOutputSchema = false)
    public Map<String, Object> getFlightStatus(
            @McpToolParam(description = "Flight number, e.g. 6E203 (required)", required = false) String flightNumber) {
        if (flightNumber == null || flightNumber.isBlank()) {
            return Map.of("error", "flightNumber is required");
        }
        FlightRecord flight = data.get(flightNumber);
        if (flight == null) {
            return Map.of("error", "No flight found with number " + flightNumber);
        }
        return toMap(flight);
    }

    @McpTool(description = "List today's departing flights from Delhi airport, with scheduled time, gate and status", generateOutputSchema = false)
    public List<Map<String, Object>> listDepartures() {
        return data.listByDirection(FlightRecord.Direction.DEPARTURE).stream().map(this::toMap).toList();
    }

    @McpTool(description = "List today's arriving flights into Delhi airport, with scheduled time, gate and status", generateOutputSchema = false)
    public List<Map<String, Object>> listArrivals() {
        return data.listByDirection(FlightRecord.Direction.ARRIVAL).stream().map(this::toMap).toList();
    }

    @McpTool(description = "Get the gate assigned to a specific flight number", generateOutputSchema = false)
    public Map<String, Object> getGateForFlight(
            @McpToolParam(description = "Flight number, e.g. 6E203 (required)", required = false) String flightNumber) {
        if (flightNumber == null || flightNumber.isBlank()) {
            return Map.of("error", "flightNumber is required");
        }
        FlightRecord flight = data.get(flightNumber);
        if (flight == null) {
            return Map.of("error", "No flight found with number " + flightNumber);
        }
        return Map.of("flightNumber", flight.getFlightNumber(), "gate", flight.getGate());
    }

    private Map<String, Object> toMap(FlightRecord f) {
        return Map.of(
                "flightNumber", f.getFlightNumber(),
                "airline", f.getAirline(),
                "origin", f.getOrigin(),
                "destination", f.getDestination(),
                "scheduledTime", f.getScheduledTime().format(TIME_FMT),
                "status", f.getStatus().name(),
                "gate", f.getGate(),
                "delayMinutes", f.getDelayMinutes()
        );
    }
}
