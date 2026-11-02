package com.example.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends BaseAdapter implements Filterable {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private final Context context;
    private final List<Project> originalProjects;
    private final List<Project> filteredProjects;
    private final LayoutInflater inflater;
    private final OnProjectClickListener clickListener;
    private ProjectFilter filter;

    public ProjectAdapter(Context context, List<Project> projects, OnProjectClickListener clickListener) {
        this.context = context;
        this.originalProjects = projects != null ? projects : new ArrayList<>();
        this.filteredProjects = new ArrayList<>(this.originalProjects);
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        return filteredProjects.size();
    }

    @Override
    public Project getItem(int position) {
        return filteredProjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<Project> newProjects) {
        this.originalProjects.clear();
        if (newProjects != null) {
            this.originalProjects.addAll(newProjects);
        }
        this.filteredProjects.clear();
        this.filteredProjects.addAll(this.originalProjects);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_project, parent, false);
            holder = new ViewHolder();
            holder.tag = convertView.findViewById(R.id.projectTag);
            holder.name = convertView.findViewById(R.id.projectName);
            holder.description = convertView.findViewById(R.id.projectDescription);
            holder.taskFraction = convertView.findViewById(R.id.projectTaskFraction);
            holder.percentage = convertView.findViewById(R.id.projectProgressPercentage);
            holder.progressBar = convertView.findViewById(R.id.projectProgressBar);
            holder.attachmentCount = convertView.findViewById(R.id.projectAttachmentCount);
            holder.commentCount = convertView.findViewById(R.id.projectCommentCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Project project = getItem(position);
        if (project != null) {
            holder.tag.setText(project.getTag() != null ? project.getTag().toUpperCase(Locale.getDefault()) : "");
            holder.name.setText(project.getName());
            holder.description.setText(project.getDescription());

            int completed = project.getCompletedTasks();
            int total = project.getTotalTasks();
            int progress = project.getProgressPercentage();

            holder.taskFraction.setText(completed + "/" + total + " tasks");
            holder.percentage.setText(progress + "%");
            holder.progressBar.setProgress(progress);

            holder.attachmentCount.setText(String.valueOf(project.getAttachmentCount()));
            holder.commentCount.setText(String.valueOf(project.getCommentCount()));

            convertView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectClick(project);
                }
            });
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ProjectFilter();
        }
        return filter;
    }

    private static class ViewHolder {
        TextView tag;
        TextView name;
        TextView description;
        TextView taskFraction;
        TextView percentage;
        ProgressBar progressBar;
        TextView attachmentCount;
        TextView commentCount;
    }

    private class ProjectFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Project> filteredList = new ArrayList<>();

            if (constraint == null || constraint.toString().trim().isEmpty()) {
                filteredList.addAll(originalProjects);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                for (Project p : originalProjects) {
                    boolean matchName = p.getName() != null && p.getName().toLowerCase(Locale.getDefault()).contains(filterPattern);
                    boolean matchTag = p.getTag() != null && p.getTag().toLowerCase(Locale.getDefault()).contains(filterPattern);
                    if (matchName || matchTag) {
                        filteredList.add(p);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredProjects.clear();
            if (results.values != null) {
                filteredProjects.addAll((List<Project>) results.values);
            }
            notifyDataSetChanged();
        }
    }
}
