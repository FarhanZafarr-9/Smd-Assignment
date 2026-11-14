package com.example.firstapp;

public enum TaskGroupType {
    OVERDUE("Overdue", R.drawable.bg_dot_overdue),
    IN_PROGRESS("In progress", R.drawable.bg_dot_inprogress),
    TODO("To do", R.drawable.bg_dot),
    DONE("Done", R.drawable.bg_dot_done);

    private final String title;
    private final int dotDrawableRes;

    TaskGroupType(String title, int dotDrawableRes) {
        this.title = title;
        this.dotDrawableRes = dotDrawableRes;
    }

    public String getTitle() {
        return title;
    }

    public int getDotDrawableRes() {
        return dotDrawableRes;
    }

    public static TaskGroupType getGroupForTask(Task task) {
        if (task == null) return TODO;
        if (task.getStatus() == TaskStatus.DONE) {
            return DONE;
        }
        if (task.isOverdue()) {
            return OVERDUE;
        }
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            return IN_PROGRESS;
        }
        return TODO;
    }
}
