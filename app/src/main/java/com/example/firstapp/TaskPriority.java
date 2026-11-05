package com.example.firstapp;

public enum TaskPriority {
    HIGH("High"),
    NORMAL("Normal"),
    LOW("Low");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskPriority fromDisplayName(String name) {
        for (TaskPriority p : values()) {
            if (p.displayName.equalsIgnoreCase(name) || p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return NORMAL;
    }
}
