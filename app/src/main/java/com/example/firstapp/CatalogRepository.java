package com.example.firstapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogRepository {

    private static final List<CatalogItem> CATALOG_ITEMS = new ArrayList<>();

    static {
        CATALOG_ITEMS.add(new CatalogItem(
                "cat_1",
                "Brand Logo.png",
                AttachmentType.IMAGE,
                R.drawable.splash_logo,
                null
        ));

        CATALOG_ITEMS.add(new CatalogItem(
                "cat_2",
                "Workspace Icon.png",
                AttachmentType.IMAGE,
                R.drawable.workspace_icon,
                null
        ));

        CATALOG_ITEMS.add(new CatalogItem(
                "cat_3",
                "Project Requirements.txt",
                AttachmentType.TEXT,
                0,
                "PROJECT REQUIREMENTS SPECIFICATION\n\n" +
                "1. Objective:\n" +
                "   Deliver a scalable, mobile-first project management system.\n\n" +
                "2. Core Modules:\n" +
                "   - Multi-tab project workspace (Overview, Tasks, Files, Discussion)\n" +
                "   - Dynamic status tracking (To do, In progress, Done, Overdue)\n" +
                "   - Interactive file catalog & content preview modal\n\n" +
                "3. Performance Targets:\n" +
                "   - Smooth 60fps scrolling on ListView\n" +
                "   - Instant filter and search response (<16ms)\n" +
                "   - Strict data encapsulation per project"
        ));

        CATALOG_ITEMS.add(new CatalogItem(
                "cat_4",
                "App UI Wireframe.png",
                AttachmentType.IMAGE,
                R.drawable.add_task,
                null
        ));

        CATALOG_ITEMS.add(new CatalogItem(
                "cat_5",
                "Architecture Guidelines.txt",
                AttachmentType.TEXT,
                0,
                "ARCHITECTURE GUIDELINES\n\n" +
                "- Activity & Fragment Responsibilities:\n" +
                "  ProjectActivity acts as host coordinator.\n" +
                "  Fragments operate on isolated model state.\n\n" +
                "- State Preservation:\n" +
                "  Tab search, filters, and sort states must persist across tab switches.\n\n" +
                "- Result Dispatch:\n" +
                "  Activity Result API updates existing project record in HomeActivity."
        ));

        CATALOG_ITEMS.add(new CatalogItem(
                "cat_6",
                "API Documentation.txt",
                AttachmentType.TEXT,
                0,
                "API DOCUMENTATION\n\n" +
                "Base URL: https://api.workspace.internal/v1\n" +
                "Authorization: Bearer <token>\n\n" +
                "Endpoints:\n" +
                "  GET  /projects          Fetch all projects\n" +
                "  POST /projects          Create new project\n" +
                "  GET  /projects/{id}     Get project by ID\n" +
                "  PUT  /projects/{id}     Update project\n" +
                "  POST /tasks             Create task\n" +
                "  PUT  /tasks/{id}        Update task status"
        ));
    }

    public static List<CatalogItem> getAllItems() {
        return Collections.unmodifiableList(CATALOG_ITEMS);
    }

    public static CatalogItem getItemById(String catalogId) {
        if (catalogId == null) return null;
        for (CatalogItem item : CATALOG_ITEMS) {
            if (catalogId.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }
}
