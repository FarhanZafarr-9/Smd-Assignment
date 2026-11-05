package com.example.firstapp;

public enum TaskStatus {
    TODO("To do"),
    IN_PROGRESS("In progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromDisplayName(String name) {
        for (TaskStatus s : values()) {
            if (s.displayName.equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return TODO;
    }
}
