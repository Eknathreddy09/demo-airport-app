package com.waisl.grms.data;

public class GroundStaffShift {
    private final String shift;
    private final int totalStaff;
    private final int availableStaff;

    public GroundStaffShift(String shift, int totalStaff, int availableStaff) {
        this.shift = shift;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public String getShift() { return shift; }
    public int getTotalStaff() { return totalStaff; }
    public int getAvailableStaff() { return availableStaff; }
}
