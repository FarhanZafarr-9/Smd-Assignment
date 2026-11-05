package com.example.firstapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TasksFragment extends Fragment implements TaskAdapter.TaskActionListener {

    private EditText editSearchTasks;
    private ImageView buttonClearTaskSearch;
    private Spinner spinnerFilterStatus;
    private Spinner spinnerFilterPriority;
    private Spinner spinnerFilterDate;
    private Spinner spinnerFilterSort;
    private TextView textTaskShowingCount;
    private TextView buttonResetFilters;

    private ListView listViewTasks;
    private View layoutEmptyNoTasks;
    private View layoutEmptyNoMatch;
    private Button buttonEmptyAddTask;
    private Button buttonEmptyResetFilters;
    private Button buttonFloatingAddTask;

    private TaskAdapter taskAdapter;
    private final List<Task> displayedTasks = new ArrayList<>();

    // Filter states
    private String currentSearch = "";
    private int currentStatusIndex = 0;   // 0: All statuses
    private int currentPriorityIndex = 0; // 0: All priorities
    private int currentDateIndex = 0;     // 0: All dates
    private int currentSortIndex = 0;     // 0: Due date ascending

    public static TasksFragment newInstance(String projectId) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putString("project_id", projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        bindViews(view);
        setupSpinners();
        setupAdapter();
        setupListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyFiltersAndSort();
    }

    private void bindViews(View v) {
        editSearchTasks = v.findViewById(R.id.editSearchTasks);
        buttonClearTaskSearch = v.findViewById(R.id.buttonClearTaskSearch);
        spinnerFilterStatus = v.findViewById(R.id.spinnerFilterStatus);
        spinnerFilterPriority = v.findViewById(R.id.spinnerFilterPriority);
        spinnerFilterDate = v.findViewById(R.id.spinnerFilterDate);
        spinnerFilterSort = v.findViewById(R.id.spinnerFilterSort);
        textTaskShowingCount = v.findViewById(R.id.textTaskShowingCount);
        buttonResetFilters = v.findViewById(R.id.buttonResetFilters);

        listViewTasks = v.findViewById(R.id.listViewTasks);
        layoutEmptyNoTasks = v.findViewById(R.id.layoutEmptyNoTasks);
        layoutEmptyNoMatch = v.findViewById(R.id.layoutEmptyNoMatch);
        buttonEmptyAddTask = v.findViewById(R.id.buttonEmptyAddTask);
        buttonEmptyResetFilters = v.findViewById(R.id.buttonEmptyResetFilters);
        buttonFloatingAddTask = v.findViewById(R.id.buttonFloatingAddTask);
    }

    private void setupSpinners() {
        if (!isAdded()) return;

        // Status
        String[] statusOptions = {"All statuses", "To do", "In progress", "Done"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, statusOptions);
        statusAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFilterStatus.setAdapter(statusAdapter);

        // Priority
        String[] priorityOptions = {"All priorities", "High", "Normal", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, priorityOptions);
        priorityAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFilterPriority.setAdapter(priorityAdapter);

        // Date
        String[] dateOptions = {"All dates", "Today", "Overdue", "No due date"};
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, dateOptions);
        dateAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFilterDate.setAdapter(dateAdapter);

        // Sort (default: Due date ascending)
        String[] sortOptions = {"Due date ascending", "Due date descending", "Priority (high to low)", "Created (newest)"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_selected, sortOptions);
        sortAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFilterSort.setAdapter(sortAdapter);

        spinnerFilterStatus.setSelection(currentStatusIndex);
        spinnerFilterPriority.setSelection(currentPriorityIndex);
        spinnerFilterDate.setSelection(currentDateIndex);
        spinnerFilterSort.setSelection(currentSortIndex);
    }

    private void setupAdapter() {
        taskAdapter = new TaskAdapter(requireContext(), displayedTasks, this);
        listViewTasks.setAdapter(taskAdapter);
    }

    private void setupListeners() {
        // Search
        editSearchTasks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().trim();
                buttonClearTaskSearch.setVisibility(currentSearch.isEmpty() ? View.GONE : View.VISIBLE);
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonClearTaskSearch.setOnClickListener(v -> editSearchTasks.setText(""));

        // Spinner Listeners
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatusIndex = spinnerFilterStatus.getSelectedItemPosition();
                currentPriorityIndex = spinnerFilterPriority.getSelectedItemPosition();
                currentDateIndex = spinnerFilterDate.getSelectedItemPosition();
                currentSortIndex = spinnerFilterSort.getSelectedItemPosition();
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFilterStatus.setOnItemSelectedListener(spinnerListener);
        spinnerFilterPriority.setOnItemSelectedListener(spinnerListener);
        spinnerFilterDate.setOnItemSelectedListener(spinnerListener);
        spinnerFilterSort.setOnItemSelectedListener(spinnerListener);

        // Reset Filters Button
        buttonResetFilters.setOnClickListener(v -> resetAllFilters());
        buttonEmptyResetFilters.setOnClickListener(v -> resetAllFilters());

        // Add Task Buttons
        buttonFloatingAddTask.setOnClickListener(v -> openAddTaskDialog());
        buttonEmptyAddTask.setOnClickListener(v -> openAddTaskDialog());
    }

    public void resetAllFilters() {
        currentSearch = "";
        editSearchTasks.setText("");
        currentStatusIndex = 0;
        currentPriorityIndex = 0;
        currentDateIndex = 0;
        currentSortIndex = 0;

        spinnerFilterStatus.setSelection(0);
        spinnerFilterPriority.setSelection(0);
        spinnerFilterDate.setSelection(0);
        spinnerFilterSort.setSelection(0);

        applyFiltersAndSort();
    }

    public void applyFiltersAndSort() {
        if (!isAdded()) return;
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        List<Task> allTasks = project.getTaskList();
        int totalProjectTasks = allTasks.size();

        displayedTasks.clear();

        // Apply filters
        for (Task t : allTasks) {
            // 1. Search Filter
            if (!currentSearch.isEmpty()) {
                String desc = t.getDescription().toLowerCase(Locale.getDefault());
                if (!desc.contains(currentSearch.toLowerCase(Locale.getDefault()))) {
                    continue;
                }
            }

            // 2. Status Filter: 0: All, 1: To do, 2: In progress, 3: Done
            if (currentStatusIndex == 1 && t.getStatus() != TaskStatus.TODO) {
                continue;
            } else if (currentStatusIndex == 2 && t.getStatus() != TaskStatus.IN_PROGRESS) {
                continue;
            } else if (currentStatusIndex == 3 && t.getStatus() != TaskStatus.DONE) {
                continue;
            }

            // 3. Priority Filter: 0: All, 1: High, 2: Normal, 3: Low
            if (currentPriorityIndex == 1 && t.getPriority() != TaskPriority.HIGH) {
                continue;
            } else if (currentPriorityIndex == 2 && t.getPriority() != TaskPriority.NORMAL) {
                continue;
            } else if (currentPriorityIndex == 3 && t.getPriority() != TaskPriority.LOW) {
                continue;
            }

            // 4. Date Filter: 0: All, 1: Today, 2: Overdue, 3: No due date
            if (currentDateIndex == 1 && !t.isDueToday()) {
                continue;
            } else if (currentDateIndex == 2 && !t.isOverdue()) {
                continue;
            } else if (currentDateIndex == 3 && t.getDueDate() != null) {
                continue;
            }

            displayedTasks.add(t);
        }

        // Apply Sort
        // 0: Due date ascending, 1: Due date descending, 2: Priority (high to low), 3: Created (newest)
        Collections.sort(displayedTasks, (t1, t2) -> {
            if (currentSortIndex == 0) {
                // Due date ascending: tasks with dates first, nulls at end
                if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                if (t1.getDueDate() == null) return 1;
                if (t2.getDueDate() == null) return -1;
                return t1.getDueDate().compareTo(t2.getDueDate());
            } else if (currentSortIndex == 1) {
                // Due date descending: tasks with dates first, nulls at end
                if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                if (t1.getDueDate() == null) return 1;
                if (t2.getDueDate() == null) return -1;
                return t2.getDueDate().compareTo(t1.getDueDate());
            } else if (currentSortIndex == 2) {
                // Priority (High -> Normal -> Low)
                return Integer.compare(getPriorityRank(t1.getPriority()), getPriorityRank(t2.getPriority()));
            } else {
                // Created newest
                return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
            }
        });

        taskAdapter.notifyDataSetChanged();

        // Update "Showing X of Y tasks"
        textTaskShowingCount.setText("Showing " + displayedTasks.size() + " of " + totalProjectTasks + " tasks");

        // Handle Empty States
        if (totalProjectTasks == 0) {
            listViewTasks.setVisibility(View.GONE);
            layoutEmptyNoTasks.setVisibility(View.VISIBLE);
            layoutEmptyNoMatch.setVisibility(View.GONE);
            buttonFloatingAddTask.setVisibility(View.GONE);
        } else if (displayedTasks.isEmpty()) {
            listViewTasks.setVisibility(View.GONE);
            layoutEmptyNoTasks.setVisibility(View.GONE);
            layoutEmptyNoMatch.setVisibility(View.VISIBLE);
            buttonFloatingAddTask.setVisibility(View.VISIBLE);
        } else {
            listViewTasks.setVisibility(View.VISIBLE);
            layoutEmptyNoTasks.setVisibility(View.GONE);
            layoutEmptyNoMatch.setVisibility(View.GONE);
            buttonFloatingAddTask.setVisibility(View.VISIBLE);
        }
    }

    private int getPriorityRank(TaskPriority priority) {
        if (priority == TaskPriority.HIGH) return 1;
        if (priority == TaskPriority.NORMAL) return 2;
        return 3;
    }

    private void openAddTaskDialog() {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        TaskDialogHelper.showTaskDialog(requireContext(), null, (task, isNew) -> {
            project.addTask(task);
            host.onProjectUpdated();
            applyFiltersAndSort();
            Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
        });
    }

    // TaskActionListener implementations
    @Override
    public void onTaskCheckChanged(Task task, boolean isChecked) {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;

        task.setStatus(isChecked ? TaskStatus.DONE : TaskStatus.TODO);
        host.onProjectUpdated();
        applyFiltersAndSort();
    }

    @Override
    public void onTaskEdit(Task task) {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;

        TaskDialogHelper.showTaskDialog(requireContext(), task, (updatedTask, isNew) -> {
            host.getProject().updateTask(updatedTask);
            host.onProjectUpdated();
            applyFiltersAndSort();
            Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onTaskDelete(Task task) {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    host.getProject().deleteTask(task.getId());
                    host.onProjectUpdated();
                    applyFiltersAndSort();
                    Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Navigation callbacks from Overview
    public void applyStatusFilter(TaskStatus status) {
        if (status == TaskStatus.TODO) {
            currentStatusIndex = 1;
        } else if (status == TaskStatus.IN_PROGRESS) {
            currentStatusIndex = 2;
        } else if (status == TaskStatus.DONE) {
            currentStatusIndex = 3;
        } else {
            currentStatusIndex = 0;
        }

        // Reset date filter to All dates
        currentDateIndex = 0;

        if (spinnerFilterStatus != null) {
            spinnerFilterStatus.setSelection(currentStatusIndex);
        }
        if (spinnerFilterDate != null) {
            spinnerFilterDate.setSelection(0);
        }

        applyFiltersAndSort();
    }

    public void applyOverdueFilter() {
        // Status: All statuses (index 0)
        currentStatusIndex = 0;
        // Date: Overdue (index 2)
        currentDateIndex = 2;

        if (spinnerFilterStatus != null) {
            spinnerFilterStatus.setSelection(0);
        }
        if (spinnerFilterDate != null) {
            spinnerFilterDate.setSelection(2);
        }

        applyFiltersAndSort();
    }
}
