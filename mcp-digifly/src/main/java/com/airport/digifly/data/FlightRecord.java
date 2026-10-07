package com.airport.digifly.data;

import java.time.LocalDateTime;

public class FlightRecord {

    public enum Direction { DEPARTURE, ARRIVAL }
    public enum Status { ON_TIME, DELAYED, BOARDING, DEPARTED, ARRIVED, CANCELLED }

    private final String flightNumber;
    private final String airline;
    private final String origin;
    private final String destination;
    private final Direction direction;
    private LocalDateTime scheduledTime;
    private Status status;
    private String gate;
    private int delayMinutes;

    public FlightRecord(String flightNumber, String airline, String origin, String destination,
                         Direction direction, LocalDateTime scheduledTime, String gate) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.direction = direction;
        this.scheduledTime = scheduledTime;
        this.gate = gate;
        this.status = Status.ON_TIME;
        this.delayMinutes = 0;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getAirline() { return airline; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public Direction getDirection() { return direction; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public int getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }
}
