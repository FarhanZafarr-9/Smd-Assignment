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

import java.util.ArrayList;

public class AddTaskScreen extends AppCompatActivity {

    private EditText inputNewTask1, inputNewTask2, inputNewTask3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputNewTask1 = findViewById(R.id.inputNewTask1);
        inputNewTask2 = findViewById(R.id.inputNewTask2);
        inputNewTask3 = findViewById(R.id.inputNewTask3);
        Button buttonAddTasks = findViewById(R.id.buttonAddTasks);

        buttonAddTasks.setOnClickListener(v -> submitTasks());
    }

    private void submitTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        collectTask(inputNewTask1, tasks);
        collectTask(inputNewTask2, tasks);
        collectTask(inputNewTask3, tasks);

        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("new_tasks", tasks);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void collectTask(EditText editText, ArrayList<String> tasks) {
        String text = editText.getText().toString().trim();
        if (!text.isEmpty()) {
            tasks.add(text);
        }
    }
}