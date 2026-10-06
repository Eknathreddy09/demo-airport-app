package com.waisl.gab.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MockBillingData {

    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();
    private final AtomicInteger invoiceSequence = new AtomicInteger(5000);

    @PostConstruct
    void seed() {
        add("6E", "Landing charges", 125000, LocalDate.now().minusDays(10), Invoice.Status.PAID);
        add("AI", "Parking bay charges", 98000, LocalDate.now().minusDays(20), Invoice.Status.OVERDUE);
        add("SG", "Ground handling charges", 54000, LocalDate.now().minusDays(3), Invoice.Status.PENDING);
        add("UK", "Fuel throughput fee", 210000, LocalDate.now().minusDays(35), Invoice.Status.OVERDUE);
        add("6E", "Terminal usage charges", 76000, LocalDate.now().minusDays(1), Invoice.Status.PENDING);
    }

    private void add(String entityId, String serviceType, double amount, LocalDate issued, Invoice.Status status) {
        String id = "INV" + invoiceSequence.incrementAndGet();
        invoices.put(id, new Invoice(id, entityId, serviceType, amount, issued, status));
    }

    public Invoice getInvoice(String invoiceId) {
        return invoices.get(invoiceId.toUpperCase());
    }

    public List<Invoice> byEntity(String entityId) {
        return invoices.values().stream()
                .filter(i -> i.getEntityId().equalsIgnoreCase(entityId))
                .toList();
    }

    public List<Invoice> outstandingByAirline(String airlineCode) {
        return byEntity(airlineCode).stream()
                .filter(i -> i.getStatus() != Invoice.Status.PAID)
                .toList();
    }

    public Invoice generateCharge(String entityId, String serviceType, double amount) {
        String id = "INV" + invoiceSequence.incrementAndGet();
        Invoice invoice = new Invoice(id, entityId, serviceType, amount, LocalDate.now(), Invoice.Status.PENDING);
        invoices.put(id, invoice);
        return invoice;
    }

    public void markOverdue(String invoiceId) {
        Invoice inv = getInvoice(invoiceId);
        if (inv != null) inv.setStatus(Invoice.Status.OVERDUE);
    }
}
