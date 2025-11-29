package com.example.tournament.model;

public enum SessionStatus {
    ACTIVE("active"),
    EXPIRED("expired"),
    REVOKED("revoked");

    private final String value;

    SessionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SessionStatus fromValue(String value) {
        for (SessionStatus status : SessionStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown session status: " + value);
    }
}
