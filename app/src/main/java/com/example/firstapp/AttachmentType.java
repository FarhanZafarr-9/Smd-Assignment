package com.example.firstapp;

public enum AttachmentType {
    IMAGE("Images"),
    TEXT("Text"),
    PDF("PDF");

    private final String displayName;

    AttachmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
