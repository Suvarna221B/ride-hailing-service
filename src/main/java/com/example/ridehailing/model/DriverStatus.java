package com.example.ridehailing.model;

public enum DriverStatus {
    AVAILABLE("available"),
    BUSY("busy"),
    OFFLINE("offline");

    private final String value;

    DriverStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DriverStatus fromString(String status) {
        for (DriverStatus driverStatus : DriverStatus.values()) {
            if (driverStatus.value.equalsIgnoreCase(status)) {
                return driverStatus;
            }
        }
        throw new IllegalArgumentException("Invalid driver status: " + status);
    }
}
