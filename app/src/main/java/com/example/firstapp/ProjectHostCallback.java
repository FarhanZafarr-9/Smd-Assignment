package com.example.firstapp;

public interface ProjectHostCallback {
    Project getProject();
    void onProjectUpdated();
    void selectTab(int position);
    void selectTasksWithStatusFilter(TaskStatus status);
    void selectTasksWithOverdueFilter();
    void openAddTaskDialog();
    void openEditProjectDialog();
}
