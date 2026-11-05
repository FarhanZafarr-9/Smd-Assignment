package com.example.firstapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project implements Serializable {

    private String id;
    private String name;
    private String tag;
    private String description;
    private List<Task> tasks;
    private List<AttachmentLink> attachmentLinks;
    private int commentCount;

    public Project(String tag, String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.tasks = new ArrayList<>();
        this.attachmentLinks = new ArrayList<>();
        this.commentCount = 0;
    }

    public Project(String tag, String name, String description, String attachments) {
        this(tag, name, description);
        if (attachments != null && !attachments.trim().isEmpty()) {
            String[] parts = attachments.split(",");
            for (String p : parts) {
                String clean = p.trim();
                if (!clean.isEmpty()) {
                    this.attachmentLinks.add(new AttachmentLink(clean, 0));
                }
            }
        }
    }

    public Project(String id, String tag, String name, String description, List<Task> tasks, List<AttachmentLink> attachmentLinks, int commentCount) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.tasks = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
        this.attachmentLinks = attachmentLinks != null ? new ArrayList<>(attachmentLinks) : new ArrayList<>();
        this.commentCount = commentCount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTaskList() {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
    }

    public List<AttachmentLink> getAttachmentLinks() {
        if (attachmentLinks == null) {
            attachmentLinks = new ArrayList<>();
        }
        return attachmentLinks;
    }

    public void setAttachmentLinks(List<AttachmentLink> attachmentLinks) {
        this.attachmentLinks = attachmentLinks != null ? new ArrayList<>(attachmentLinks) : new ArrayList<>();
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    // Task calculations
    public int getTotalTasks() {
        return tasks != null ? tasks.size() : 0;
    }

    public int getCompletedTasks() {
        if (tasks == null) return 0;
        int completed = 0;
        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.DONE) {
                completed++;
            }
        }
        return completed;
    }

    public int getTodoTasks() {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.TODO) {
                count++;
            }
        }
        return count;
    }

    public int getInProgressTasks() {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.IN_PROGRESS) {
                count++;
            }
        }
        return count;
    }

    public int getOverdueTasks() {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    public int getProgressPercentage() {
        int total = getTotalTasks();
        if (total == 0) {
            return 0;
        }
        return (getCompletedTasks() * 100) / total;
    }

    public int getAttachmentCount() {
        return attachmentLinks != null ? attachmentLinks.size() : 0;
    }

    public String getTaskCountDisplay() {
        return getCompletedTasks() + "/" + getTotalTasks();
    }

    // Mutation helpers
    public void addTask(Task task) {
        if (task == null) return;
        if (tasks == null) tasks = new ArrayList<>();
        tasks.add(task);
    }

    public void addTask(String taskText) {
        if (taskText == null || taskText.trim().isEmpty()) return;
        addTask(new Task(taskText.trim(), TaskStatus.TODO, TaskPriority.NORMAL, null));
    }

    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || tasks == null) return false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(updatedTask.getId())) {
                tasks.set(i, updatedTask);
                return true;
            }
        }
        return false;
    }

    public boolean deleteTask(String taskId) {
        if (taskId == null || tasks == null) return false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(taskId)) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addAttachmentLink(AttachmentLink link) {
        if (link == null) return;
        if (attachmentLinks == null) attachmentLinks = new ArrayList<>();
        attachmentLinks.add(link);
    }

    public boolean removeAttachmentLink(String linkId) {
        if (linkId == null || attachmentLinks == null) return false;
        for (int i = 0; i < attachmentLinks.size(); i++) {
            if (attachmentLinks.get(i).getId().equals(linkId)) {
                attachmentLinks.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean hasAttachment(String catalogId) {
        if (catalogId == null || attachmentLinks == null) return false;
        for (AttachmentLink link : attachmentLinks) {
            if (catalogId.equals(link.getCatalogId())) {
                return true;
            }
        }
        return false;
    }

    // Backward-compatibility adapters for legacy code
    public void setTasks(String[] taskTexts) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        } else {
            this.tasks.clear();
        }
        if (taskTexts != null) {
            for (String t : taskTexts) {
                if (t != null && !t.trim().isEmpty()) {
                    this.tasks.add(new Task(t.trim(), TaskStatus.TODO, TaskPriority.NORMAL, null));
                }
            }
        }
    }

    public String[] getTasks() {
        if (tasks == null || tasks.isEmpty()) {
            return new String[0];
        }
        String[] result = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            result[i] = tasks.get(i).getDescription();
        }
        return result;
    }

    public boolean isTaskDone(int index) {
        if (tasks == null || index < 0 || index >= tasks.size()) return false;
        return tasks.get(index).getStatus() == TaskStatus.DONE;
    }

    public void setTaskDone(int index, boolean done) {
        if (tasks == null || index < 0 || index >= tasks.size()) return;
        tasks.get(index).setStatus(done ? TaskStatus.DONE : TaskStatus.TODO);
    }

    public void setCompletedTasks(int completedTasks) {
        // Handled automatically by tasks' statuses
    }

    public String[] getAttachmentList() {
        if (attachmentLinks == null || attachmentLinks.isEmpty()) {
            return new String[0];
        }
        String[] result = new String[attachmentLinks.size()];
        for (int i = 0; i < attachmentLinks.size(); i++) {
            String catId = attachmentLinks.get(i).getCatalogId();
            CatalogItem item = CatalogRepository.getItemById(catId);
            result[i] = item != null ? item.getDisplayName() : catId;
        }
        return result;
    }
}
