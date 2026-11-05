package com.example.firstapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends BaseAdapter {

    public interface TaskActionListener {
        void onTaskCheckChanged(Task task, boolean isChecked);
        void onTaskEdit(Task task);
        void onTaskDelete(Task task);
    }

    private final Context context;
    private final List<Task> tasks;
    private final LayoutInflater inflater;
    private final TaskActionListener listener;

    public TaskAdapter(Context context, List<Task> tasks, TaskActionListener listener) {
        this.context = context;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks.clear();
        if (newTasks != null) {
            this.tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.checkboxTaskDone);
            holder.description = convertView.findViewById(R.id.textTaskDescription);
            holder.buttonEdit = convertView.findViewById(R.id.buttonEditTask);
            holder.buttonDelete = convertView.findViewById(R.id.buttonDeleteTask);
            holder.status = convertView.findViewById(R.id.textTaskStatus);
            holder.priority = convertView.findViewById(R.id.textTaskPriority);
            holder.dueDate = convertView.findViewById(R.id.textTaskDueDate);
            holder.overdueBadge = convertView.findViewById(R.id.textTaskOverdueBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);
        if (task != null) {
            holder.description.setText(task.getDescription());

            // Avoid triggering listener during binding
            holder.checkBox.setOnCheckedChangeListener(null);
            boolean isDone = (task.getStatus() == TaskStatus.DONE);
            holder.checkBox.setChecked(isDone);

            // Strikethrough for Done while keeping it clearly readable
            if (isDone) {
                holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.description.setTextColor(Color.parseColor("#6B7280"));
            } else {
                holder.description.setPaintFlags(holder.description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.description.setTextColor(Color.parseColor("#1F2937"));
            }

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCheckChanged(task, isChecked);
                }
            });

            // Status Badge
            holder.status.setText(task.getStatus().getDisplayName());
            if (task.getStatus() == TaskStatus.DONE) {
                holder.status.setTextColor(Color.parseColor("#2E7D32"));
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                holder.status.setTextColor(Color.parseColor("#E65100"));
            } else {
                holder.status.setTextColor(Color.parseColor("#5E35B1"));
            }

            // Priority Badge
            holder.priority.setText(task.getPriority().getDisplayName());
            if (task.getPriority() == TaskPriority.HIGH) {
                holder.priority.setTextColor(Color.parseColor("#C62828"));
            } else if (task.getPriority() == TaskPriority.LOW) {
                holder.priority.setTextColor(Color.parseColor("#757575"));
            } else {
                holder.priority.setTextColor(Color.parseColor("#1565C0"));
            }

            // Due Date
            holder.dueDate.setText(task.getFormattedDueDate());

            // Overdue Badge (unfinished and past-due)
            holder.overdueBadge.setVisibility(task.isOverdue() ? View.VISIBLE : View.GONE);

            // Action listeners
            holder.buttonEdit.setOnClickListener(v -> {
                if (listener != null) listener.onTaskEdit(task);
            });

            holder.buttonDelete.setOnClickListener(v -> {
                if (listener != null) listener.onTaskDelete(task);
            });

            convertView.setOnClickListener(v -> {
                if (listener != null) listener.onTaskEdit(task);
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView description;
        ImageView buttonEdit;
        ImageView buttonDelete;
        TextView status;
        TextView priority;
        TextView dueDate;
        TextView overdueBadge;
    }
}
