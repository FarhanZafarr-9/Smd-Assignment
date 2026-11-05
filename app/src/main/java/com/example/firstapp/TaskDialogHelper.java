package com.example.firstapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDialogHelper {

    public interface OnTaskSavedListener {
        void onTaskSaved(Task task, boolean isNew);
    }

    public static void showTaskDialog(Context context, Task existingTask, OnTaskSavedListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_editor, null);

        TextView textTitle = dialogView.findViewById(R.id.textTaskDialogTitle);
        TextView textCharCounter = dialogView.findViewById(R.id.textTaskCharCounter);
        EditText editDescription = dialogView.findViewById(R.id.editTaskDescription);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerTaskPriority);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerTaskStatus);
        TextView textSelectedDueDate = dialogView.findViewById(R.id.textSelectedDueDate);
        Button buttonPickDueDate = dialogView.findViewById(R.id.buttonPickDueDate);
        ImageView buttonClearDueDate = dialogView.findViewById(R.id.buttonClearDueDate);

        boolean isNew = (existingTask == null);
        textTitle.setText(isNew ? "Add Task" : "Edit Task");

        // Setup Spinners
        String[] priorities = {"Normal", "High", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, priorities);
        spinnerPriority.setAdapter(priorityAdapter);

        String[] statuses = {"To do", "In progress", "Done"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statusAdapter);

        final Long[] selectedDueDate = new Long[1];
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        // Prepopulate if editing
        if (!isNew) {
            editDescription.setText(existingTask.getDescription());
            textCharCounter.setText(existingTask.getDescription().length() + "/500");

            if (existingTask.getPriority() == TaskPriority.HIGH) {
                spinnerPriority.setSelection(1);
            } else if (existingTask.getPriority() == TaskPriority.LOW) {
                spinnerPriority.setSelection(2);
            } else {
                spinnerPriority.setSelection(0);
            }

            if (existingTask.getStatus() == TaskStatus.IN_PROGRESS) {
                spinnerStatus.setSelection(1);
            } else if (existingTask.getStatus() == TaskStatus.DONE) {
                spinnerStatus.setSelection(2);
            } else {
                spinnerStatus.setSelection(0);
            }

            if (existingTask.getDueDate() != null) {
                selectedDueDate[0] = existingTask.getDueDate();
                textSelectedDueDate.setText(sdf.format(new Date(selectedDueDate[0])));
                buttonClearDueDate.setVisibility(View.VISIBLE);
            }
        } else {
            // Default Priority: Normal (index 0), Status: To do (index 0)
            spinnerPriority.setSelection(0);
            spinnerStatus.setSelection(0);
            textCharCounter.setText("0/500");
        }

        // Live Counter
        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textCharCounter.setText(s.length() + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Date Picker Action
        buttonPickDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDueDate[0] != null) {
                calendar.setTimeInMillis(selectedDueDate[0]);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(context, (view, y, m, d) -> {
                Calendar chosen = Calendar.getInstance();
                chosen.set(Calendar.YEAR, y);
                chosen.set(Calendar.MONTH, m);
                chosen.set(Calendar.DAY_OF_MONTH, d);
                chosen.set(Calendar.HOUR_OF_DAY, 0);
                chosen.set(Calendar.MINUTE, 0);
                chosen.set(Calendar.SECOND, 0);
                chosen.set(Calendar.MILLISECOND, 0);

                selectedDueDate[0] = chosen.getTimeInMillis();
                textSelectedDueDate.setText(sdf.format(new Date(selectedDueDate[0])));
                buttonClearDueDate.setVisibility(View.VISIBLE);
            }, year, month, day);

            datePicker.show();
        });

        // Clear Date Action
        buttonClearDueDate.setOnClickListener(v -> {
            selectedDueDate[0] = null;
            textSelectedDueDate.setText("No due date");
            buttonClearDueDate.setVisibility(View.GONE);
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(isNew ? "Add" : "Save", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String desc = editDescription.getText().toString().trim();
                if (desc.isEmpty() || desc.length() > 500) {
                    editDescription.setError("Description required (1–500 chars)");
                    return;
                }

                String prioStr = (String) spinnerPriority.getSelectedItem();
                TaskPriority priority = TaskPriority.fromDisplayName(prioStr);

                String statusStr = (String) spinnerStatus.getSelectedItem();
                TaskStatus status = TaskStatus.fromDisplayName(statusStr);

                Task taskToSave;
                if (isNew) {
                    taskToSave = new Task(desc, status, priority, selectedDueDate[0]);
                } else {
                    taskToSave = existingTask;
                    taskToSave.setDescription(desc);
                    taskToSave.setPriority(priority);
                    taskToSave.setStatus(status);
                    taskToSave.setDueDate(selectedDueDate[0]);
                }

                if (listener != null) {
                    listener.onTaskSaved(taskToSave, isNew);
                }
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}
