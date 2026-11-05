package com.example.firstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TasksFragment extends Fragment {

    private TaskStatus pendingStatusFilter;
    private boolean pendingOverdueFilter;

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
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    public void applyStatusFilter(TaskStatus status) {
        this.pendingStatusFilter = status;
        this.pendingOverdueFilter = false;
        // Will be applied to controls in full TasksFragment implementation
    }

    public void applyOverdueFilter() {
        this.pendingStatusFilter = null;
        this.pendingOverdueFilter = true;
        // Will be applied to controls in full TasksFragment implementation
    }
}
