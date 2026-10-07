package com.airport.grms.data;

public class GateAssignment {
    private final String flightNumber;
    private String gate;
    private String groundEquipment;

    public GateAssignment(String flightNumber, String gate) {
        this.flightNumber = flightNumber;
        this.gate = gate;
        this.groundEquipment = "none";
    }

    public String getFlightNumber() { return flightNumber; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public String getGroundEquipment() { return groundEquipment; }
    public void setGroundEquipment(String groundEquipment) { this.groundEquipment = groundEquipment; }
}
