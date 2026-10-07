package com.airport.gab.tools;

import com.airport.gab.data.Invoice;
import com.airport.gab.data.MockBillingData;
import com.airport.gab.events.BillingEventPublisher;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BillingTools {

    private final MockBillingData data;
    private final BillingEventPublisher publisher;

    public BillingTools(MockBillingData data, BillingEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @McpTool(description = "Get a specific invoice by invoice ID", generateOutputSchema = false)
    public Map<String, Object> getInvoice(
            @McpToolParam(description = "Invoice ID, e.g. INV5001 (required)", required = false) String invoiceId) {
        if (invoiceId == null || invoiceId.isBlank()) {
            return Map.of("error", "invoiceId is required");
        }
        Invoice invoice = data.getInvoice(invoiceId);
        if (invoice == null) return Map.of("error", "No invoice found with ID " + invoiceId);
        return toMap(invoice);
    }

    @McpTool(description = "Get all billing records and overall billing status for an entity (airline code, e.g. 6E, AI, SG, UK)", generateOutputSchema = false)
    public List<Map<String, Object>> getBillingStatus(
            @McpToolParam(description = "Entity/airline code, e.g. 6E (required)", required = false) String entityId) {
        if (entityId == null || entityId.isBlank()) {
            return List.of(Map.of("error", "entityId is required"));
        }
        return data.byEntity(entityId).stream().map(this::toMap).toList();
    }

    @McpTool(description = "Generate a new charge/invoice for an entity for a given service type and amount", generateOutputSchema = false)
    public Map<String, Object> generateCharge(
            @McpToolParam(description = "Entity/airline code, e.g. 6E (required)", required = false) String entityId,
            @McpToolParam(description = "Type of service being charged, e.g. Landing charges (required)", required = false) String serviceType,
            @McpToolParam(description = "Amount to charge (required)", required = false) String amount) {
        if (entityId == null || entityId.isBlank()) {
            return Map.of("error", "entityId is required");
        }
        if (serviceType == null || serviceType.isBlank()) {
            return Map.of("error", "serviceType is required");
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
        Invoice invoice = data.generateCharge(entityId, serviceType, amountValue);
        Map<String, Object> payload = toMap(invoice);
        publisher.publish("billing.invoice_created", payload);
        return payload;
    }

    @McpTool(description = "List all outstanding (pending or overdue) dues for an airline", generateOutputSchema = false)
    public List<Map<String, Object>> listOutstandingDues(
            @McpToolParam(description = "Airline code, e.g. 6E, AI, SG, UK (required)", required = false) String airlineCode) {
        if (airlineCode == null || airlineCode.isBlank()) {
            return List.of(Map.of("error", "airlineCode is required"));
        }
        return data.outstandingByAirline(airlineCode).stream().map(this::toMap).toList();
    }

    private Map<String, Object> toMap(Invoice i) {
        return Map.of(
                "invoiceId", i.getInvoiceId(),
                "entityId", i.getEntityId(),
                "serviceType", i.getServiceType(),
                "amount", i.getAmount(),
                "issuedDate", i.getIssuedDate().toString(),
                "status", i.getStatus().name()
        );
    }
}
