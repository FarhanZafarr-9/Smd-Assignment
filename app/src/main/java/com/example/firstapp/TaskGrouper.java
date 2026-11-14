package com.example.firstapp;

import java.util.ArrayList;
import java.util.List;

public class TaskGrouper {

    private static final TaskGroupType[] FIXED_GROUP_ORDER = new TaskGroupType[] {
            TaskGroupType.OVERDUE,
            TaskGroupType.IN_PROGRESS,
            TaskGroupType.TODO,
            TaskGroupType.DONE
    };

    /**
     * Groups tasks according to fixed order (Overdue, In progress, To do, Done).
     * Only non-empty groups are included with their section header.
     */
    public static List<TaskListItem> groupTasks(List<Task> tasks) {
        List<TaskListItem> result = new ArrayList<>();
        if (tasks == null || tasks.isEmpty()) {
            return result;
        }

        // Bucket tasks by group
        List<Task> overdueList = new ArrayList<>();
        List<Task> inProgressList = new ArrayList<>();
        List<Task> todoList = new ArrayList<>();
        List<Task> doneList = new ArrayList<>();

        for (Task task : tasks) {
            TaskGroupType group = TaskGroupType.getGroupForTask(task);
            switch (group) {
                case OVERDUE:
                    overdueList.add(task);
                    break;
                case IN_PROGRESS:
                    inProgressList.add(task);
                    break;
                case TODO:
                    todoList.add(task);
                    break;
                case DONE:
                    doneList.add(task);
                    break;
            }
        }

        // Add non-empty groups in strict fixed order
        for (TaskGroupType groupType : FIXED_GROUP_ORDER) {
            List<Task> currentBucket;
            switch (groupType) {
                case OVERDUE:
                    currentBucket = overdueList;
                    break;
                case IN_PROGRESS:
                    currentBucket = inProgressList;
                    break;
                case TODO:
                    currentBucket = todoList;
                    break;
                case DONE:
                default:
                    currentBucket = doneList;
                    break;
            }

            if (!currentBucket.isEmpty()) {
                result.add(new TaskListItem.HeaderItem(groupType, currentBucket.size()));
                for (Task t : currentBucket) {
                    result.add(new TaskListItem.TaskRowItem(t, groupType));
                }
            }
        }

        return result;
    }
}
