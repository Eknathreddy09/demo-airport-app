package com.waisl.gab.data;

import java.time.LocalDate;

public class Invoice {
    public enum Status { PAID, PENDING, OVERDUE }

    private final String invoiceId;
    private final String entityId;
    private final String serviceType;
    private final double amount;
    private final LocalDate issuedDate;
    private Status status;

    public Invoice(String invoiceId, String entityId, String serviceType, double amount,
                    LocalDate issuedDate, Status status) {
        this.invoiceId = invoiceId;
        this.entityId = entityId;
        this.serviceType = serviceType;
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.status = status;
    }

    public String getInvoiceId() { return invoiceId; }
    public String getEntityId() { return entityId; }
    public String getServiceType() { return serviceType; }
    public double getAmount() { return amount; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
