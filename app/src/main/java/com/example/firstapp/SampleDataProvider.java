package com.example.firstapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SampleDataProvider {

    public static Project createInitialSampleProject() {
        Project project = new Project(
                "MOBILE-APP",
                "Smd Assignment 3",
                "Comprehensive task and project tracking system with multi-tab workspace, catalog attachments, and real-time status filtering."
        );

        // 12 Sample Tasks
        List<Task> tasks = new ArrayList<>();

        // 4 Done Tasks
        tasks.add(new Task(
                "Define project brand color palette and typography",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                daysFromToday(-4)
        ));
        tasks.add(new Task(
                "Setup Android project architecture and Gradle dependencies",
                TaskStatus.DONE,
                TaskPriority.NORMAL,
                daysFromToday(-3)
        ));
        tasks.add(new Task(
                "Create vector assets and splash screen layout",
                TaskStatus.DONE,
                TaskPriority.NORMAL,
                daysFromToday(-2)
        ));
        tasks.add(new Task(
                "Design database entity relationships and models",
                TaskStatus.DONE,
                TaskPriority.LOW,
                daysFromToday(-1)
        ));

        // 3 In Progress Tasks (1 Overdue, 1 Today, 1 Future)
        tasks.add(new Task(
                "Implement custom ListView adapter for Home workspace",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                daysFromToday(0) // Due Today
        ));
        tasks.add(new Task(
                "Develop multi-tab navigation with ViewPager2",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                daysFromToday(2) // Future
        ));
        tasks.add(new Task(
                "Resolve layout clipping on smaller resolution devices",
                TaskStatus.IN_PROGRESS,
                TaskPriority.NORMAL,
                daysFromToday(-2) // Overdue
        ));

        // 5 To Do Tasks (1 Overdue, 2 Future, 2 No Due Date)
        tasks.add(new Task(
                "Implement real-time search and filter controls",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                daysFromToday(-3) // Overdue
        ));
        tasks.add(new Task(
                "Build file catalog attachment picker dialog",
                TaskStatus.TODO,
                TaskPriority.NORMAL,
                daysFromToday(1) // Future
        ));
        tasks.add(new Task(
                "Configure edge-to-edge window insets handling",
                TaskStatus.TODO,
                TaskPriority.LOW,
                daysFromToday(5) // Future
        ));
        tasks.add(new Task(
                "Write unit tests for progress calculation and date parsing",
                TaskStatus.TODO,
                TaskPriority.NORMAL,
                null // No due date
        ));
        tasks.add(new Task(
                "Perform end-to-end user acceptance testing",
                TaskStatus.TODO,
                TaskPriority.LOW,
                null // No due date
        ));

        project.setTasks(tasks);

        // 3 Initial Files (2 Images, 1 Text)
        List<AttachmentLink> links = new ArrayList<>();
        links.add(new AttachmentLink("cat_1", R.drawable.splash_logo));
        links.add(new AttachmentLink("cat_2", R.drawable.workspace_icon));
        links.add(new AttachmentLink("cat_3", 0));
        project.setAttachmentLinks(links);

        // Discussion count = 3
        project.setCommentCount(3);

        return project;
    }

    private static Long daysFromToday(int offsetDays) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, offsetDays);
        return cal.getTimeInMillis();
    }
}
