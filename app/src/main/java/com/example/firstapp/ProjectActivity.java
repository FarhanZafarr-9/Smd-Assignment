package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ProjectActivity extends AppCompatActivity {

    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        project = (Project) getIntent().getSerializableExtra("project");
    }

    protected void finishWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_project", project);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
