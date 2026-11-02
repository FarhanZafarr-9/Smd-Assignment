package com.example.firstapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Task implements Serializable {

    private String id;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long dueDate; // epoch millis at start of day, or null if no due date
    private long createdAt;

    public Task(String description, TaskStatus status, TaskPriority priority, Long dueDate) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
        this.priority = priority != null ? priority : TaskPriority.NORMAL;
        this.dueDate = dueDate;
        this.createdAt = System.currentTimeMillis();
    }

    public Task(String id, String description, TaskStatus status, TaskPriority priority, Long dueDate, long createdAt) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
        this.priority = priority != null ? priority : TaskPriority.NORMAL;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Overdue means the due date is before today and the status isn't Done.
     * A task due today, or with no date, isn't overdue.
     */
    public boolean isOverdue() {
        if (dueDate == null || status == TaskStatus.DONE) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar taskDate = Calendar.getInstance();
        taskDate.setTimeInMillis(dueDate);
        taskDate.set(Calendar.HOUR_OF_DAY, 0);
        taskDate.set(Calendar.MINUTE, 0);
        taskDate.set(Calendar.SECOND, 0);
        taskDate.set(Calendar.MILLISECOND, 0);

        return taskDate.before(today);
    }

    public boolean isDueToday() {
        if (dueDate == null) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar taskDate = Calendar.getInstance();
        taskDate.setTimeInMillis(dueDate);
        taskDate.set(Calendar.HOUR_OF_DAY, 0);
        taskDate.set(Calendar.MINUTE, 0);
        taskDate.set(Calendar.SECOND, 0);
        taskDate.set(Calendar.MILLISECOND, 0);

        return taskDate.getTimeInMillis() == today.getTimeInMillis();
    }

    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "No due date";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(dueDate));
    }
}
