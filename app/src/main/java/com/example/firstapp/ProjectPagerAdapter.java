package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProjectPagerAdapter extends FragmentStateAdapter {

    private final String projectId;
    private OverviewFragment overviewFragment;
    private TasksFragment tasksFragment;
    private FilesFragment filesFragment;
    private DiscussionFragment discussionFragment;

    public ProjectPagerAdapter(@NonNull FragmentActivity fragmentActivity, String projectId) {
        super(fragmentActivity);
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                overviewFragment = OverviewFragment.newInstance(projectId);
                return overviewFragment;
            case 1:
                tasksFragment = TasksFragment.newInstance(projectId);
                return tasksFragment;
            case 2:
                filesFragment = FilesFragment.newInstance(projectId);
                return filesFragment;
            case 3:
            default:
                discussionFragment = DiscussionFragment.newInstance(projectId);
                return discussionFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public OverviewFragment getOverviewFragment() {
        return overviewFragment;
    }

    public TasksFragment getTasksFragment() {
        return tasksFragment;
    }

    public FilesFragment getFilesFragment() {
        return filesFragment;
    }

    public DiscussionFragment getDiscussionFragment() {
        return discussionFragment;
    }
}
