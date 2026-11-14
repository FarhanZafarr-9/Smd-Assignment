package com.example.firstapp;

public abstract class TaskListItem {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_TASK = 1;

    public abstract int getViewType();

    public static class HeaderItem extends TaskListItem {
        private final TaskGroupType groupType;
        private final int taskCount;

        public HeaderItem(TaskGroupType groupType, int taskCount) {
            this.groupType = groupType;
            this.taskCount = taskCount;
        }

        @Override
        public int getViewType() {
            return VIEW_TYPE_HEADER;
        }

        public TaskGroupType getGroupType() {
            return groupType;
        }

        public int getTaskCount() {
            return taskCount;
        }
    }

    public static class TaskRowItem extends TaskListItem {
        private final Task task;
        private final TaskGroupType groupType;

        public TaskRowItem(Task task, TaskGroupType groupType) {
            this.task = task;
            this.groupType = groupType;
        }

        @Override
        public int getViewType() {
            return VIEW_TYPE_TASK;
        }

        public Task getTask() {
            return task;
        }

        public TaskGroupType getGroupType() {
            return groupType;
        }
    }
}
