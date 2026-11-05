package com.example.firstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class OverviewFragment extends Fragment {

    private TextView textOverviewDescription;
    private TextView textOverviewPercentage;
    private ProgressBar progressBarOverview;
    private TextView textOverviewFraction;
    private TextView buttonEditProject;

    private View cardBreakdownTodo;
    private TextView countBreakdownTodo;
    private View cardBreakdownInProgress;
    private TextView countBreakdownInProgress;
    private View cardBreakdownDone;
    private TextView countBreakdownDone;
    private View cardBreakdownOverdue;
    private TextView countBreakdownOverdue;

    private View quickLinkAttachments;
    private TextView textQuickLinkAttachments;
    private View quickLinkDiscussion;
    private TextView textQuickLinkDiscussion;
    private Button buttonOverviewAddTask;

    public static OverviewFragment newInstance(String projectId) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString("project_id", projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        bindViews(view);
        setupListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshViews();
    }

    private void bindViews(View v) {
        textOverviewDescription = v.findViewById(R.id.textOverviewDescription);
        textOverviewPercentage = v.findViewById(R.id.textOverviewPercentage);
        progressBarOverview = v.findViewById(R.id.progressBarOverview);
        textOverviewFraction = v.findViewById(R.id.textOverviewFraction);
        buttonEditProject = v.findViewById(R.id.buttonEditProject);

        cardBreakdownTodo = v.findViewById(R.id.cardBreakdownTodo);
        countBreakdownTodo = v.findViewById(R.id.countBreakdownTodo);
        cardBreakdownInProgress = v.findViewById(R.id.cardBreakdownInProgress);
        countBreakdownInProgress = v.findViewById(R.id.countBreakdownInProgress);
        cardBreakdownDone = v.findViewById(R.id.cardBreakdownDone);
        countBreakdownDone = v.findViewById(R.id.countBreakdownDone);
        cardBreakdownOverdue = v.findViewById(R.id.cardBreakdownOverdue);
        countBreakdownOverdue = v.findViewById(R.id.countBreakdownOverdue);

        quickLinkAttachments = v.findViewById(R.id.quickLinkAttachments);
        textQuickLinkAttachments = v.findViewById(R.id.textQuickLinkAttachments);
        quickLinkDiscussion = v.findViewById(R.id.quickLinkDiscussion);
        textQuickLinkDiscussion = v.findViewById(R.id.textQuickLinkDiscussion);
        buttonOverviewAddTask = v.findViewById(R.id.buttonOverviewAddTask);
    }

    public void refreshViews() {
        if (!isAdded()) return;
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        textOverviewDescription.setText(project.getDescription());

        int total = project.getTotalTasks();
        int completed = project.getCompletedTasks();
        int progress = project.getProgressPercentage();

        textOverviewFraction.setText(completed + " of " + total + " tasks completed");
        textOverviewPercentage.setText(progress + "%");
        progressBarOverview.setProgress(progress);

        countBreakdownTodo.setText(String.valueOf(project.getTodoTasks()));
        countBreakdownInProgress.setText(String.valueOf(project.getInProgressTasks()));
        countBreakdownDone.setText(String.valueOf(completed));
        countBreakdownOverdue.setText(String.valueOf(project.getOverdueTasks()));

        textQuickLinkAttachments.setText("Attachments (" + project.getAttachmentCount() + ")");
        textQuickLinkDiscussion.setText("Discussion (" + project.getCommentCount() + ")");
    }

    private void setupListeners() {
        // Edit Project Dialog
        buttonEditProject.setOnClickListener(v -> showEditProjectDialog());

        // Breakdown Clicks -> Select Tasks tab with matching filter
        cardBreakdownTodo.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTasksWithStatusFilter(TaskStatus.TODO);
            }
        });

        cardBreakdownInProgress.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTasksWithStatusFilter(TaskStatus.IN_PROGRESS);
            }
        });

        cardBreakdownDone.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTasksWithStatusFilter(TaskStatus.DONE);
            }
        });

        cardBreakdownOverdue.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTasksWithOverdueFilter();
            }
        });

        // Quick Links
        quickLinkAttachments.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTab(2); // Files tab
            }
        });

        quickLinkDiscussion.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host != null) {
                host.selectTab(3); // Discussion tab
            }
        });

        // Add Task Action (stays on Overview, refreshes summaries and tab count)
        buttonOverviewAddTask.setOnClickListener(v -> {
            ProjectHostCallback host = (ProjectHostCallback) getActivity();
            if (host == null) return;
            Project project = host.getProject();
            if (project == null) return;

            TaskDialogHelper.showTaskDialog(requireContext(), null, (task, isNew) -> {
                project.addTask(task);
                host.onProjectUpdated();
                refreshViews();
                Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showEditProjectDialog() {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_project, null);
        EditText editName = dialogView.findViewById(R.id.editEditProjectName);
        EditText editTag = dialogView.findViewById(R.id.editEditProjectTag);
        EditText editDesc = dialogView.findViewById(R.id.editEditProjectDescription);

        editName.setText(project.getName());
        editTag.setText(project.getTag());
        editDesc.setText(project.getDescription());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveBtn.setOnClickListener(v -> {
                String name = editName.getText().toString().trim();
                String tag = editTag.getText().toString().trim();
                String desc = editDesc.getText().toString().trim();

                boolean valid = true;
                if (name.isEmpty() || name.length() > 60) {
                    editName.setError("Name required (1–60 chars)");
                    valid = false;
                }
                if (tag.isEmpty() || tag.length() > 20) {
                    editTag.setError("Tag required (1–20 chars)");
                    valid = false;
                }
                if (desc.isEmpty() || desc.length() > 500) {
                    editDesc.setError("Description required (1–500 chars)");
                    valid = false;
                }
                if (!valid) return;

                project.setName(name);
                project.setTag(tag);
                project.setDescription(desc);

                host.onProjectUpdated();
                refreshViews();
                dialog.dismiss();
                Toast.makeText(requireContext(), "Project updated", Toast.LENGTH_SHORT).show();
            });
        });

        dialog.show();
    }
}
