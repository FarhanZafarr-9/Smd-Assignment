package com.example.firstapp;

import java.io.Serializable;

public class CatalogItem implements Serializable {

    private String id;
    private String displayName;
    private AttachmentType type;
    private int drawableResId; // For images
    private String textContent;  // For text content

    public CatalogItem(String id, String displayName, AttachmentType type, int drawableResId, String textContent) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.drawableResId = drawableResId;
        this.textContent = textContent;
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
}
