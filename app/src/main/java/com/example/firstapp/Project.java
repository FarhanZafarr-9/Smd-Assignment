package com.example.firstapp;

import java.io.Serializable;

public class Project implements Serializable {

    private final String tag;
    private final String name;
    private final String description;
    private final String attachments; // comma-separated string, e.g. "file1.pdf,photo2.png"
    private int completedTasks;
    private int totalTasks;
    private final int commentCount;
    private String[] tasks;
    private boolean[] tasksDone;

    private static final String[] DEFAULT_TASKS = {
            "Create a Design guideline for the brand",
            "Create other base resources for the project"
    };

    public Project(String tag, String name, String description, String attachments) {
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.attachments = attachments;
        this.tasks = DEFAULT_TASKS;
        this.tasksDone = new boolean[DEFAULT_TASKS.length];
        this.completedTasks = 0;
        this.totalTasks = DEFAULT_TASKS.length;
        this.commentCount = 0;
    }

    // Getters
    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public String[] getTasks() {
        return tasks;
    }

    public boolean isTaskDone(int index) {
        if (tasksDone == null || index < 0 || index >= tasksDone.length) return false;
        return tasksDone[index];
    }

    public void addTask(String taskText) {
        if (taskText == null || taskText.trim().isEmpty()) return;
        String[] newTasks = new String[tasks.length + 1];
        boolean[] newDone = new boolean[tasksDone.length + 1];
        System.arraycopy(tasks, 0, newTasks, 0, tasks.length);
        System.arraycopy(tasksDone, 0, newDone, 0, tasksDone.length);
        newTasks[newTasks.length - 1] = taskText.trim();
        newDone[newDone.length - 1] = false;
        tasks = newTasks;
        tasksDone = newDone;
        totalTasks = newTasks.length;
    }

    public void setTaskDone(int index, boolean done) {
        if (tasksDone == null || index < 0 || index >= tasksDone.length) return;
        tasksDone[index] = done;
        int completed = 0;
        for (boolean b : tasksDone) {
            if (b) completed++;
        }
        this.completedTasks = completed;
    }

    public void setTasks(String[] newTasks) {
        if (newTasks == null || newTasks.length == 0) return;
        this.tasks = newTasks;
        this.tasksDone = new boolean[newTasks.length];
        this.totalTasks = newTasks.length;
        this.completedTasks = 0;
    }

    public String[] getAttachmentList() {
        if (attachments == null || attachments.trim().isEmpty()) {
            return new String[0];
        }
        return attachments.split(",");
    }

    public int getAttachmentCount() {
        return getAttachmentList().length;
    }

    public String getTaskCountDisplay() {
        return completedTasks + "/" + totalTasks;
    }
}
