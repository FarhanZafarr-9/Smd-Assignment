package com.example.firstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Objects;

public class ProjectAdapter extends ListAdapter<Project, ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private static final DiffUtil.ItemCallback<Project> DIFF_CALLBACK = new DiffUtil.ItemCallback<Project>() {
        @Override
        public boolean areItemsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getTag(), newItem.getTag())
                    && Objects.equals(oldItem.getDescription(), newItem.getDescription())
                    && oldItem.getCompletedTasks() == newItem.getCompletedTasks()
                    && oldItem.getTotalTasks() == newItem.getTotalTasks()
                    && oldItem.getProgressPercentage() == newItem.getProgressPercentage()
                    && oldItem.getAttachmentCount() == newItem.getAttachmentCount()
                    && oldItem.getCommentCount() == newItem.getCommentCount();
        }
    };

    private final OnProjectClickListener clickListener;

    public ProjectAdapter(OnProjectClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = getItem(position);
        holder.bind(project, clickListener);
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView tag;
        private final TextView name;
        private final TextView description;
        private final TextView taskFraction;
        private final TextView percentage;
        private final ProgressBar progressBar;
        private final TextView attachmentCount;
        private final TextView commentCount;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.projectTag);
            name = itemView.findViewById(R.id.projectName);
            description = itemView.findViewById(R.id.projectDescription);
            taskFraction = itemView.findViewById(R.id.projectTaskFraction);
            percentage = itemView.findViewById(R.id.projectProgressPercentage);
            progressBar = itemView.findViewById(R.id.projectProgressBar);
            attachmentCount = itemView.findViewById(R.id.projectAttachmentCount);
            commentCount = itemView.findViewById(R.id.projectCommentCount);
        }

        void bind(Project project, OnProjectClickListener clickListener) {
            if (project == null) return;
            tag.setText(project.getTag() != null ? project.getTag().toUpperCase(Locale.getDefault()) : "");
            name.setText(project.getName());
            description.setText(project.getDescription());

            int completed = project.getCompletedTasks();
            int total = project.getTotalTasks();
            int progress = project.getProgressPercentage();

            taskFraction.setText(completed + "/" + total + " tasks");
            percentage.setText(progress + "%");
            progressBar.setProgress(progress);

            attachmentCount.setText(String.valueOf(project.getAttachmentCount()));
            commentCount.setText(String.valueOf(project.getCommentCount()));

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectClick(project);
                }
            });
        }
    }
}
