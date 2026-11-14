package com.example.firstapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupedTasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface TaskActionListener {
        void onTaskCheckChanged(Task task, boolean isChecked);
        void onTaskEdit(Task task);
        void onTaskDelete(Task task, int position);
    }

    private final List<TaskListItem> items = new ArrayList<>();
    private final TaskActionListener listener;

    public GroupedTasksAdapter(TaskActionListener listener) {
        this.listener = listener;
    }

    public List<TaskListItem> getItems() {
        return items;
    }

    public void submitItems(List<TaskListItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TaskListItem.VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_task_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder && item instanceof TaskListItem.HeaderItem) {
            ((HeaderViewHolder) holder).bind((TaskListItem.HeaderItem) item);
        } else if (holder instanceof TaskViewHolder && item instanceof TaskListItem.TaskRowItem) {
            ((TaskViewHolder) holder).bind(((TaskListItem.TaskRowItem) item).getTask(), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final View viewHeaderIndicator;
        private final TextView textHeaderStatusName;
        private final TextView textHeaderTaskCount;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewHeaderIndicator = itemView.findViewById(R.id.viewHeaderIndicator);
            textHeaderStatusName = itemView.findViewById(R.id.textHeaderStatusName);
            textHeaderTaskCount = itemView.findViewById(R.id.textHeaderTaskCount);
        }

        public void bind(TaskListItem.HeaderItem headerItem) {
            TaskGroupType group = headerItem.getGroupType();
            viewHeaderIndicator.setBackgroundResource(group.getDotDrawableRes());
            textHeaderStatusName.setText(group.getTitle());
            int count = headerItem.getTaskCount();
            textHeaderTaskCount.setText(count + (count == 1 ? " task" : " tasks"));
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView description;
        private final ImageView buttonEdit;
        private final ImageView buttonDelete;
        private final TextView status;
        private final TextView priority;
        private final TextView dueDate;
        private final TextView overdueBadge;

        private Task boundTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxTaskDone);
            description = itemView.findViewById(R.id.textTaskDescription);
            buttonEdit = itemView.findViewById(R.id.buttonEditTask);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteTask);
            status = itemView.findViewById(R.id.textTaskStatus);
            priority = itemView.findViewById(R.id.textTaskPriority);
            dueDate = itemView.findViewById(R.id.textTaskDueDate);
            overdueBadge = itemView.findViewById(R.id.textTaskOverdueBadge);
        }

        public Task getTask() {
            return boundTask;
        }

        public void bind(Task task, TaskActionListener listener) {
            this.boundTask = task;
            if (task == null) return;

            description.setText(task.getDescription());

            // Avoid listener triggers during binding
            checkBox.setOnCheckedChangeListener(null);
            boolean isDone = (task.getStatus() == TaskStatus.DONE);
            checkBox.setChecked(isDone);

            // Strikethrough for Done
            if (isDone) {
                description.setPaintFlags(description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                description.setTextColor(Color.parseColor("#6B7280"));
            } else {
                description.setPaintFlags(description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                description.setTextColor(Color.parseColor("#1F2937"));
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCheckChanged(task, isChecked);
                }
            });

            // Status Badge
            status.setText(task.getStatus().getDisplayName());
            if (task.getStatus() == TaskStatus.DONE) {
                status.setTextColor(Color.parseColor("#2E7D32"));
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                status.setTextColor(Color.parseColor("#E65100"));
            } else {
                status.setTextColor(Color.parseColor("#5E35B1"));
            }

            // Priority Badge
            priority.setText(task.getPriority().getDisplayName());
            if (task.getPriority() == TaskPriority.HIGH) {
                priority.setTextColor(Color.parseColor("#C62828"));
            } else if (task.getPriority() == TaskPriority.LOW) {
                priority.setTextColor(Color.parseColor("#757575"));
            } else {
                priority.setTextColor(Color.parseColor("#1565C0"));
            }

            // Due Date
            dueDate.setText(task.getFormattedDueDate());

            // Overdue Badge
            overdueBadge.setVisibility(task.isOverdue() ? View.VISIBLE : View.GONE);

            buttonEdit.setOnClickListener(v -> {
                if (listener != null) listener.onTaskEdit(task);
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) listener.onTaskDelete(task, getAdapterPosition());
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTaskEdit(task);
            });
        }
    }
}
