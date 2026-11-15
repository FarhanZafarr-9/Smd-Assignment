package com.example.firstapp;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void testProgressCalculationEmptyProject() {
        Project emptyProject = new Project("TAG", "Empty", "No tasks");
        assertEquals(0, emptyProject.getTotalTasks());
        assertEquals(0, emptyProject.getCompletedTasks());
        assertEquals(0, emptyProject.getProgressPercentage());
    }

    @Test
    public void testProgressCalculationSampleProject() {
        Project sample = SampleDataProvider.createInitialSampleProject();
        assertEquals(12, sample.getTotalTasks());
        assertEquals(4, sample.getCompletedTasks());
        // 4 / 12 * 100 = 33.33 -> fraction dropped = 33%
        assertEquals(33, sample.getProgressPercentage());
    }

    @Test
    public void testTaskBreakdownSumEqualsTotal() {
        Project sample = SampleDataProvider.createInitialSampleProject();
        int todo = sample.getTodoTasks();
        int inProgress = sample.getInProgressTasks();
        int done = sample.getCompletedTasks();
        int total = sample.getTotalTasks();

        assertEquals(12, total);
        assertEquals(5, todo);
        assertEquals(3, inProgress);
        assertEquals(4, done);
        assertEquals(total, todo + inProgress + done);
    }

    @Test
    public void testOverdueLogic() {
        Calendar calPast = Calendar.getInstance();
        calPast.set(Calendar.HOUR_OF_DAY, 0);
        calPast.set(Calendar.MINUTE, 0);
        calPast.set(Calendar.SECOND, 0);
        calPast.set(Calendar.MILLISECOND, 0);
        calPast.add(Calendar.DAY_OF_YEAR, -2);

        Calendar calToday = Calendar.getInstance();
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);

        Calendar calFuture = Calendar.getInstance();
        calFuture.set(Calendar.HOUR_OF_DAY, 0);
        calFuture.set(Calendar.MINUTE, 0);
        calFuture.set(Calendar.SECOND, 0);
        calFuture.set(Calendar.MILLISECOND, 0);
        calFuture.add(Calendar.DAY_OF_YEAR, 2);

        // Past due and TODO -> overdue
        Task pastTodo = new Task("Past Todo", TaskStatus.TODO, TaskPriority.HIGH, calPast.getTimeInMillis());
        assertTrue(pastTodo.isOverdue());

        // Past due and IN_PROGRESS -> overdue
        Task pastInProgress = new Task("Past InProgress", TaskStatus.IN_PROGRESS, TaskPriority.NORMAL, calPast.getTimeInMillis());
        assertTrue(pastInProgress.isOverdue());

        // Past due but DONE -> NOT overdue
        Task pastDone = new Task("Past Done", TaskStatus.DONE, TaskPriority.NORMAL, calPast.getTimeInMillis());
        assertFalse(pastDone.isOverdue());

        // Due today -> NOT overdue
        Task todayTask = new Task("Today Task", TaskStatus.TODO, TaskPriority.NORMAL, calToday.getTimeInMillis());
        assertFalse(todayTask.isOverdue());
        assertTrue(todayTask.isDueToday());

        // Future due date -> NOT overdue
        Task futureTask = new Task("Future Task", TaskStatus.TODO, TaskPriority.NORMAL, calFuture.getTimeInMillis());
        assertFalse(futureTask.isOverdue());

        // No due date -> NOT overdue
        Task noDateTask = new Task("No Date", TaskStatus.TODO, TaskPriority.NORMAL, null);
        assertFalse(noDateTask.isOverdue());
    }

    @Test
    public void testSampleAttachmentsInitialCounts() {
        Project sample = SampleDataProvider.createInitialSampleProject();
        assertEquals(3, sample.getAttachmentCount());

        int imageCount = 0;
        int textCount = 0;
        for (AttachmentLink link : sample.getAttachmentLinks()) {
            CatalogItem item = CatalogRepository.getItemById(link.getCatalogId());
            assertNotNull(item);
            if (item.getType() == AttachmentType.IMAGE) {
                imageCount++;
            } else if (item.getType() == AttachmentType.TEXT) {
                textCount++;
            }
        }

        assertEquals(2, imageCount);
        assertEquals(1, textCount);
    }

    @Test
    public void testProjectDataIsolation() {
        Project p1 = new Project("TAG1", "Project 1", "Desc 1");
        Project p2 = new Project("TAG2", "Project 2", "Desc 2");

        p1.addTask(new Task("Task in P1", TaskStatus.TODO, TaskPriority.NORMAL, null));
        assertEquals(1, p1.getTotalTasks());
        assertEquals(0, p2.getTotalTasks());

        p1.addAttachmentLink(new AttachmentLink("cat_1", 0));
        assertEquals(1, p1.getAttachmentCount());
        assertEquals(0, p2.getAttachmentCount());
    }

    @Test
    public void testTaskGrouperOrderingAndOmission() {
        Project sample = SampleDataProvider.createInitialSampleProject();
        List<TaskListItem> grouped = TaskGrouper.groupTasks(sample.getTaskList());
        assertFalse(grouped.isEmpty());

        // Verify headers appear in order: Overdue -> In progress -> To do -> Done
        int lastGroupOrder = -1;
        for (TaskListItem item : grouped) {
            if (item instanceof TaskListItem.HeaderItem) {
                TaskListItem.HeaderItem header = (TaskListItem.HeaderItem) item;
                int currentOrder = header.getGroupType().ordinal();
                assertTrue("Headers must be in fixed order: Overdue, In progress, To do, Done", currentOrder > lastGroupOrder);
                assertTrue("Header task count must be positive", header.getTaskCount() > 0);
                lastGroupOrder = currentOrder;
            }
        }
    }

    @Test
    public void testTaskGrouperEmptyGroupSuppression() {
        List<Task> tasksOnlyDone = new java.util.ArrayList<>();
        tasksOnlyDone.add(new Task("Done Task 1", TaskStatus.DONE, TaskPriority.NORMAL, null));
        tasksOnlyDone.add(new Task("Done Task 2", TaskStatus.DONE, TaskPriority.HIGH, null));

        List<TaskListItem> grouped = TaskGrouper.groupTasks(tasksOnlyDone);
        // Only 1 header (Done) + 2 task items = 3 total items
        assertEquals(3, grouped.size());
        assertTrue(grouped.get(0) instanceof TaskListItem.HeaderItem);
        TaskListItem.HeaderItem header = (TaskListItem.HeaderItem) grouped.get(0);
        assertEquals(TaskGroupType.DONE, header.getGroupType());
        assertEquals(2, header.getTaskCount());
    }

    @Test
    public void testProjectSnapshotCopyIsolation() {
        Project original = SampleDataProvider.createInitialSampleProject();
        Project copy = original.copy();

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getTotalTasks(), copy.getTotalTasks());

        // Mutate copy
        copy.addTask(new Task("Extra Task", TaskStatus.TODO, TaskPriority.HIGH, null));
        assertEquals(original.getTotalTasks() + 1, copy.getTotalTasks());
        assertNotEquals(original.getTotalTasks(), copy.getTotalTasks());
    }
}
