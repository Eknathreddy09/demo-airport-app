package com.airport.gab.events;

import com.airport.gab.data.Invoice;
import com.airport.gab.data.MockBillingData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class BillingSimulator {

    private final MockBillingData data;
    private final BillingEventPublisher publisher;
    private final Random random = new Random();
    private static final String[] AIRLINES = {"6E", "AI", "SG", "UK"};
    private static final String[] SERVICES = {"Landing charges", "Parking bay charges", "Ground handling charges"};

    public BillingSimulator(MockBillingData data, BillingEventPublisher publisher) {
        this.data = data;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 35_000)
    public void simulateRandomEvent() {
        String airline = AIRLINES[random.nextInt(AIRLINES.length)];
        String service = SERVICES[random.nextInt(SERVICES.length)];
        double amount = 20000 + random.nextInt(150000);
        simulateInvoiceCreated(airline, service, amount);
    }

    public Map<String, Object> simulateInvoiceCreated(String entityId, String serviceType, double amount) {
        Invoice invoice = data.generateCharge(entityId, serviceType, amount);
        Map<String, Object> payload = Map.of(
                "invoiceId", invoice.getInvoiceId(),
                "entityId", invoice.getEntityId(),
                "serviceType", invoice.getServiceType(),
                "amount", invoice.getAmount()
        );
        publisher.publish("billing.invoice_created", payload);
        return payload;
    }

    public Map<String, Object> simulatePaymentOverdue(String invoiceId) {
        data.markOverdue(invoiceId);
        Map<String, Object> payload = Map.of("invoiceId", invoiceId, "status", "OVERDUE");
        publisher.publish("billing.payment_overdue", payload);
        return payload;
    }
}
