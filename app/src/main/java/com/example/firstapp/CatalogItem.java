package com.example.firstapp;

import java.io.Serializable;

public class CatalogItem implements Serializable {

    private String id;
    private String displayName;
    private AttachmentType type;
    private int drawableResId; // For drawable resource images
    private String textContent;  // For text content
    private String fileUri;     // For device files picked from storage

    public CatalogItem(String id, String displayName, AttachmentType type, int drawableResId, String textContent) {
        this(id, displayName, type, drawableResId, textContent, null);
    }

    public CatalogItem(String id, String displayName, AttachmentType type, int drawableResId, String textContent, String fileUri) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.drawableResId = drawableResId;
        this.textContent = textContent;
        this.fileUri = fileUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
}
