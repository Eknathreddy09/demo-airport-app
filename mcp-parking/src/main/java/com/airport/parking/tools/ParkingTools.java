package com.airport.parking.tools;

import com.airport.parking.data.MockParkingData;
import com.airport.parking.data.ParkingTicket;
import com.airport.parking.data.ParkingZone;
import com.airport.parking.events.ParkingEventPublisher;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ParkingTools {

    private final MockParkingData data;
    private final ParkingEventPublisher publisher;

    public ParkingTools(MockParkingData data, ParkingEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @McpTool(description = "Check available parking slots and hourly rate for a zone (e.g. T3-P1, T3-P2, T1-P1, VIP-P1), or list all zones if none given", generateOutputSchema = false)
    public List<Map<String, Object>> checkParkingAvailability(
            @McpToolParam(description = "Zone code, or leave blank to list all zones", required = false) String zone) {
        List<ParkingZone> zones = (zone == null || zone.isBlank())
                ? data.allZones().stream().toList()
                : List.of(data.getZone(zone));
        return zones.stream().filter(java.util.Objects::nonNull).map(this::toMap).toList();
    }

    @McpTool(description = "Book a parking slot in a zone for a vehicle, returns a ticket ID and amount due", generateOutputSchema = false)
    public Map<String, Object> bookParkingSlot(
            @McpToolParam(description = "Zone code, e.g. T3-P1 (required)", required = false) String zone,
            @McpToolParam(description = "Vehicle registration number (required)", required = false) String vehicleNumber,
            @McpToolParam(description = "Duration in hours (required)", required = false) String durationHours) {
        if (zone == null || zone.isBlank()) {
            return Map.of("error", "zone is required");
        }
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            return Map.of("error", "vehicleNumber is required");
        }
        int durationHoursValue;
        try {
            durationHoursValue = Integer.parseInt(durationHours == null ? "" : durationHours.trim());
        } catch (NumberFormatException e) {
            return Map.of("error", "durationHours is required and must be a whole number greater than 0");
        }
        if (durationHoursValue <= 0) {
            return Map.of("error", "durationHours is required and must be greater than 0");
        }
        ParkingTicket ticket = data.book(zone, vehicleNumber, durationHoursValue);
        if (ticket == null) {
            return Map.of("error", "No available slots in zone " + zone);
        }
        return ticketToMap(ticket);
    }

    @McpTool(description = "Get the status and payment state of a parking ticket by ticket ID", generateOutputSchema = false)
    public Map<String, Object> getParkingTicketStatus(
            @McpToolParam(description = "Ticket ID, e.g. PK1001 (required)", required = false) String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            return Map.of("error", "ticketId is required");
        }
        ParkingTicket ticket = data.getTicket(ticketId);
        if (ticket == null) {
            return Map.of("error", "No ticket found with ID " + ticketId);
        }
        return ticketToMap(ticket);
    }

    @McpTool(description = "Process payment for a parking ticket by ticket ID and amount", generateOutputSchema = false)
    public Map<String, Object> processParkingPayment(
            @McpToolParam(description = "Ticket ID, e.g. PK1001 (required)", required = false) String ticketId,
            @McpToolParam(description = "Amount being paid (required)", required = false) String amount) {
        if (ticketId == null || ticketId.isBlank()) {
            return Map.of("error", "ticketId is required");
        }
        double amountValue;
        try {
            amountValue = Double.parseDouble(amount == null ? "" : amount.trim());
        } catch (NumberFormatException e) {
            return Map.of("error", "amount is required and must be a number greater than 0");
        }
        if (amountValue <= 0) {
            return Map.of("error", "amount is required and must be greater than 0");
        }
        ParkingTicket ticket = data.getTicket(ticketId);
        if (ticket == null) {
            return Map.of("error", "No ticket found with ID " + ticketId);
        }
        boolean ok = data.pay(ticketId);
        Map<String, Object> payload = Map.of("ticketId", ticketId, "amountPaid", amountValue, "paid", ok);
        publisher.publish("parking.payment_completed", payload);
        return payload;
    }

    private Map<String, Object> toMap(ParkingZone z) {
        return Map.of(
                "zoneCode", z.getZoneCode(),
                "description", z.getDescription(),
                "totalSlots", z.getTotalSlots(),
                "availableSlots", z.getAvailableSlots(),
                "hourlyRate", z.getHourlyRate()
        );
    }

    private Map<String, Object> ticketToMap(ParkingTicket t) {
        return Map.of(
                "ticketId", t.getTicketId(),
                "zoneCode", t.getZoneCode(),
                "vehicleNumber", t.getVehicleNumber(),
                "durationHours", t.getDurationHours(),
                "amountDue", t.getAmountDue(),
                "paymentStatus", t.getPaymentStatus().name()
        );
    }
}
