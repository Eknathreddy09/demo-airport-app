package com.waisl.gab.events;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Manual event triggers for live demos - not part of the MCP tool surface. */
@RestController
public class DebugController {

    private final BillingSimulator simulator;

    public DebugController(BillingSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/debug/simulate-invoice")
    public Map<String, Object> simulateInvoice(@RequestParam String entityId,
                                                @RequestParam String serviceType,
                                                @RequestParam double amount) {
        return simulator.simulateInvoiceCreated(entityId, serviceType, amount);
    }

    @PostMapping("/debug/simulate-overdue/{invoiceId}")
    public Map<String, Object> simulateOverdue(@PathVariable String invoiceId) {
        return simulator.simulatePaymentOverdue(invoiceId);
    }
}
