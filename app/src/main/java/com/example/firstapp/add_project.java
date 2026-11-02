package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class add_project extends AppCompatActivity {

    private EditText inputProjectName, inputProjectTag, inputAttachment, inputProjectDescription;
    private EditText inputTask1, inputTask2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputProjectName = findViewById(R.id.inputProjectName);
        inputProjectTag = findViewById(R.id.inputProjectTag);
        inputAttachment = findViewById(R.id.inputAttachment);
        inputProjectDescription = findViewById(R.id.inputProjectDescription);
        inputTask1 = findViewById(R.id.inputTask1);
        inputTask2 = findViewById(R.id.inputTask2);
        Button buttonAddProject = findViewById(R.id.buttonAddProject);

        buttonAddProject.setOnClickListener(v -> submitProject());
    }

    private void submitProject() {
        String name = inputProjectName.getText().toString().trim();
        String tag = inputProjectTag.getText().toString().trim();
        String attachments = inputAttachment.getText().toString().trim();
        String description = inputProjectDescription.getText().toString().trim();
        String task1 = inputTask1.getText().toString().trim();
        String task2 = inputTask2.getText().toString().trim();

        if (name.isEmpty() || tag.isEmpty()) {
            inputProjectName.setError(name.isEmpty() ? "Required" : null);
            inputProjectTag.setError(tag.isEmpty() ? "Required" : null);
            return;
        }

        Project newProject = new Project(tag, name, description, attachments);
        newProject.setTasks(new String[]{
                task1.isEmpty() ? getString(R.string.project_task1) : task1,
                task2.isEmpty() ? getString(R.string.project_task2) : task2
        });

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_project",  newProject);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}