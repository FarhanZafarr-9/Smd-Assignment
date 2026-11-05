package com.example.firstapp;

import java.io.Serializable;
import java.util.UUID;

public class AttachmentLink implements Serializable {

    private String id;
    private String catalogId;
    private int resourceRef; // Resource ID or 0

    public AttachmentLink(String catalogId, int resourceRef) {
        this.id = UUID.randomUUID().toString();
        this.catalogId = catalogId;
        this.resourceRef = resourceRef;
    }

    public AttachmentLink(String id, String catalogId, int resourceRef) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.catalogId = catalogId;
        this.resourceRef = resourceRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public int getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(int resourceRef) {
        this.resourceRef = resourceRef;
    }
}
