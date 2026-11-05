package com.example.firstapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_CARDS = 3;
    private static final int REQUEST_ADD_PROJECT = 1;
    private static final int REQUEST_VIEW_PROJECT = 2;

    private final Project[] projects = new Project[MAX_CARDS];
    private Project currentProject;
    private int currentIndex = -1;

    private TextView emptyState;
    private LinearLayout projectsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emptyState = findViewById(R.id.emptyState);
        projectsContainer = findViewById(R.id.projectsContainer);

        Button buttonNewProject = findViewById(R.id.buttonNewProject);
        buttonNewProject.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, add_project.class);
            startActivityForResult(intent, REQUEST_ADD_PROJECT);
        });

        displayProjects();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_ADD_PROJECT) {
            Project newProject = (Project) data.getSerializableExtra("new_project");
            if (newProject != null) {
                addProject(newProject);
            }
        } else if (requestCode == REQUEST_VIEW_PROJECT && currentIndex >= 0) {
            Project updated = (Project) data.getSerializableExtra("project");
            if (updated != null) {
                projects[currentIndex] = updated;
                currentProject = updated;
            } else {
                int completed = data.getIntExtra("completed_tasks", 0);
                currentProject.setCompletedTasks(completed);
            }
            displayProjects();
        }
    }

    private void addProject(Project newProject) {
        int index = -1;
        for (int i = 0; i < projects.length; i++) {
            if (projects[i] == null) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            for (int i = 0; i < projects.length - 1; i++) {
                projects[i] = projects[i + 1];
            }
            index = projects.length - 1;
        }
        projects[index] = newProject;
        displayProjects();
    }

    private void displayProjects() {
        int n = 0;
        for (Project p : projects) {
            if (p != null) n++;
        }
        emptyState.setVisibility(n == 0 ? View.VISIBLE : View.GONE);

        projectsContainer.removeAllViews();
        for (int i = 0; i < projects.length; i++) {
            if (projects[i] != null) {
                projectsContainer.addView(createProjectCard(projects[i], i));
            }
        }
    }

    private LinearLayout createProjectCard(Project p, int index) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_container);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dp(25), dp(12), dp(25), 0);
        card.setLayoutParams(cardParams);

        TextView tag = new TextView(this);
        tag.setText(p.getTag());
        tag.setTextSize(12);
        tag.setTextColor(Color.parseColor("#7B4FA3"));
        tag.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tag.setBackgroundResource(R.drawable.bg_rounded_pill);
        tag.setPadding(dp(12), dp(4), dp(12), dp(4));
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tag.setLayoutParams(tagParams);
        card.addView(tag);

        TextView name = new TextView(this);
        name.setText(p.getName());
        name.setTextSize(18);
        name.setTextColor(Color.parseColor("#000000"));
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        name.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.setMargins(0, dp(8), 0, 0);
        name.setLayoutParams(nameParams);
        card.addView(name);

        TextView description = new TextView(this);
        description.setText(p.getDescription());
        description.setTextSize(13);
        description.setTextColor(Color.parseColor("#121212"));
        description.setAlpha(0.65f);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.setMargins(0, dp(4), 0, 0);
        description.setLayoutParams(descParams);
        card.addView(description);

        LinearLayout taskRow = new LinearLayout(this);
        taskRow.setOrientation(LinearLayout.HORIZONTAL);
        taskRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams taskRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        taskRowParams.setMargins(0, dp(12), 0, 0);
        taskRow.setLayoutParams(taskRowParams);

        ImageView checkIcon = new ImageView(this);
        checkIcon.setImageResource(R.drawable.checkmark_icon);
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.setMargins(0, 0, dp(15), 0);
        checkIcon.setLayoutParams(checkParams);
        taskRow.addView(checkIcon);

        TextView taskCount = new TextView(this);
        taskCount.setText(p.getTaskCountDisplay());
        taskCount.setTextSize(14);
        taskCount.setTextColor(Color.parseColor("#222222"));
        LinearLayout.LayoutParams taskCountParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        taskCount.setLayoutParams(taskCountParams);
        taskRow.addView(taskCount);

        TextView inviteButton = new TextView(this);
        inviteButton.setText(R.string.project_action);
        inviteButton.setTextSize(12);
        inviteButton.setTextColor(Color.WHITE);
        inviteButton.setBackgroundResource(R.drawable.bg_rounded_button);
        inviteButton.setPadding(dp(20), dp(4), dp(20), dp(4));
        taskRow.addView(inviteButton);
        card.addView(taskRow);

        LinearLayout attachRow = new LinearLayout(this);
        attachRow.setOrientation(LinearLayout.HORIZONTAL);
        attachRow.setGravity(Gravity.CENTER_VERTICAL);
        attachRow.setBackgroundResource(R.drawable.bg_attachments);
        attachRow.setPadding(dp(22), dp(12), dp(22), dp(12));
        LinearLayout.LayoutParams attachRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        attachRowParams.setMargins(0, dp(12), 0, 0);
        attachRow.setLayoutParams(attachRowParams);

        ImageView attachIcon = new ImageView(this);
        attachIcon.setImageResource(R.drawable.attachment_icon);
        LinearLayout.LayoutParams attachIconParams = new LinearLayout.LayoutParams(dp(16), dp(16));
        attachIcon.setLayoutParams(attachIconParams);
        attachRow.addView(attachIcon);

        TextView attachCount = new TextView(this);
        attachCount.setText(p.getAttachmentCount() + " Attachment");
        attachCount.setTextSize(12);
        attachCount.setTextColor(Color.parseColor("#555555"));
        LinearLayout.LayoutParams attachCountParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        attachCountParams.setMargins(dp(6), 0, 0, 0);
        attachCount.setLayoutParams(attachCountParams);
        attachRow.addView(attachCount);

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#121212"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(dp(1), dp(16));
        dividerParams.setMargins(dp(12), 0, dp(12), 0);
        divider.setLayoutParams(dividerParams);
        attachRow.addView(divider);

        ImageView commentIcon = new ImageView(this);
        commentIcon.setImageResource(R.drawable.comment_icon);
        LinearLayout.LayoutParams commentIconParams = new LinearLayout.LayoutParams(dp(16), dp(16));
        commentIcon.setLayoutParams(commentIconParams);
        attachRow.addView(commentIcon);

        TextView commentCount = new TextView(this);
        commentCount.setText(p.getCommentCount() + " Comment");
        commentCount.setTextSize(12);
        commentCount.setTextColor(Color.parseColor("#333333"));
        LinearLayout.LayoutParams commentCountParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        commentCountParams.setMargins(dp(6), 0, 0, 0);
        commentCount.setLayoutParams(commentCountParams);
        attachRow.addView(commentCount);
        card.addView(attachRow);

        card.setOnClickListener(v -> {
            currentProject = projects[index];
            currentIndex = index;
            Intent intent = new Intent(MainActivity.this, ProductScreen.class);
            intent.putExtra("project", currentProject);
            startActivityForResult(intent, REQUEST_VIEW_PROJECT);
        });

        return card;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}