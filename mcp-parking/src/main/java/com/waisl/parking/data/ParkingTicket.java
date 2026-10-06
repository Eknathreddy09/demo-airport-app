package com.waisl.parking.data;

import java.time.LocalDateTime;

public class ParkingTicket {
    public enum PaymentStatus { UNPAID, PAID }

    private final String ticketId;
    private final String zoneCode;
    private final String vehicleNumber;
    private final LocalDateTime entryTime;
    private final int durationHours;
    private double amountDue;
    private PaymentStatus paymentStatus;

    public ParkingTicket(String ticketId, String zoneCode, String vehicleNumber,
                          LocalDateTime entryTime, int durationHours, double amountDue) {
        this.ticketId = ticketId;
        this.zoneCode = zoneCode;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
        this.durationHours = durationHours;
        this.amountDue = amountDue;
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    public String getTicketId() { return ticketId; }
    public String getZoneCode() { return zoneCode; }
    public String getVehicleNumber() { return vehicleNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public int getDurationHours() { return durationHours; }
    public double getAmountDue() { return amountDue; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
