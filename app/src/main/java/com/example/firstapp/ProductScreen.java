package com.example.firstapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ProductScreen extends AppCompatActivity {

    private static final int REQUEST_ADD_TASKS = 1;

    private Project project;

    private TextView tagView, titleView, descriptionView;
    private ImageView backButton;

    private View attachmentsRow;
    private View attachmentCard1, attachmentCard2, attachmentCard3;
    private TextView attachmentTitle1, attachmentTitle2, attachmentTitle3;

    private LinearLayout tasksContainer;
    private TextView addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        project = (Project) getIntent().getSerializableExtra("project");

        bindViews();
        populateProjectInfo();
        buildTasks();

        backButton.setOnClickListener(v -> finishWithResult());
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductScreen.this, AddTaskScreen.class);
            startActivityForResult(intent, REQUEST_ADD_TASKS);
        });
    }

    private void bindViews() {
        tagView = findViewById(R.id.card1Tag);
        titleView = findViewById(R.id.projectScreenTitle);
        descriptionView = findViewById(R.id.card1Description);
        backButton = findViewById(R.id.backButton);
        addTaskButton = findViewById(R.id.addTaskButton);

        attachmentsRow = findViewById(R.id.attachmentsRow);
        attachmentCard1 = findViewById(R.id.attachmentCard1);
        attachmentCard2 = findViewById(R.id.attachmentCard2);
        attachmentCard3 = findViewById(R.id.attachmentCard3);
        attachmentTitle1 = findViewById(R.id.attachmentTitle1);
        attachmentTitle2 = findViewById(R.id.attachmentTitle2);
        attachmentTitle3 = findViewById(R.id.attachmentTitle3);

        tasksContainer = findViewById(R.id.tasksContainer);
    }

    private void populateProjectInfo() {
        if (project == null) return;

        tagView.setText(project.getTag());
        titleView.setText(project.getName());
        descriptionView.setText(project.getDescription());

        populateAttachments(project.getAttachmentList());
    }

    private void populateAttachments(String[] attachments) {
        View[] cards = {attachmentCard1, attachmentCard2, attachmentCard3};
        TextView[] titles = {attachmentTitle1, attachmentTitle2, attachmentTitle3};

        boolean hasAny = attachments != null && attachments.length > 0;
        attachmentsRow.setVisibility(hasAny ? View.VISIBLE : View.GONE);

        for (int i = 0; i < cards.length; i++) {
            if (attachments != null && i < attachments.length) {
                cards[i].setVisibility(View.VISIBLE);
                titles[i].setText(attachments[i].trim());
            } else {
                cards[i].setVisibility(View.GONE);
            }
        }
    }

    private void buildTasks() {
        tasksContainer.removeAllViews();
        String[] tasks = project.getTasks();
        for (int i = 0; i < tasks.length; i++) {
            tasksContainer.addView(createTaskRow(tasks[i], i));
        }
    }

    private LinearLayout createTaskRow(String taskText, int index) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, index > 0 ? dp(15) : 0, 0, 0);
        row.setLayoutParams(rowParams);

        ImageView emptyIcon = new ImageView(this);
        emptyIcon.setImageResource(R.drawable.unchecked_icon);
        emptyIcon.setContentDescription(getString(R.string.todo));
        row.addView(emptyIcon);

        ImageView checkedIcon = new ImageView(this);
        checkedIcon.setImageResource(R.drawable.checked_icon);
        checkedIcon.setContentDescription(getString(R.string.todo));
        row.addView(checkedIcon);

        TextView taskTextview = new TextView(this);
        taskTextview.setText(taskText);
        taskTextview.setTextSize(14);
        taskTextview.setTextColor(Color.parseColor("#121212"));
        taskTextview.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dp(10), 0, 0, 0);
        taskTextview.setLayoutParams(textParams);
        row.addView(taskTextview);

        updateCheckboxIcons(emptyIcon, checkedIcon, project.isTaskDone(index));

        row.setOnClickListener(v -> {
            if (project != null) {
                boolean now = !project.isTaskDone(index);
                project.setTaskDone(index, now);
                updateCheckboxIcons(emptyIcon, checkedIcon, now);
            }
        });

        return row;
    }

    private void updateCheckboxIcons(ImageView emptyIcon, ImageView checkedIcon, boolean isChecked) {
        emptyIcon.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        checkedIcon.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void finishWithResult() {
        int completed = 0;
        if (project != null) {
            completed = project.getCompletedTasks();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("project", project);
        resultIntent.putExtra("completed_tasks", completed);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_ADD_TASKS) {
            ArrayList<String> newTasks = data.getStringArrayListExtra("new_tasks");
            if (newTasks != null && project != null) {
                for (String task : newTasks) {
                    project.addTask(task);
                }
                buildTasks();
            }
        }
    }

    @SuppressWarnings("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        finishWithResult();
        super.onBackPressed();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}