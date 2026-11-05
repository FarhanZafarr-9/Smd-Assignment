package com.example.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Project> projectsList = new ArrayList<>();
    private ProjectAdapter adapter;

    private ListView listViewProjects;
    private TextView emptyProjectsView;
    private TextView textProjectsCount;
    private EditText editSearchProjects;
    private ImageView buttonClearSearch;

    // For file picker in New Project dialog
    private CatalogItem pendingDeviceItem;
    private TextView activeTextSelectedAttachment;
    private View activeButtonClearAttachment;

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    CatalogItem item = AttachmentPickerHelper.processPickedUri(this, uri);
                    if (item != null) {
                        pendingDeviceItem = item;
                        if (activeTextSelectedAttachment != null) {
                            activeTextSelectedAttachment.setText(item.getDisplayName() + " (" + item.getType().getDisplayName() + ")");
                        }
                        if (activeButtonClearAttachment != null) {
                            activeButtonClearAttachment.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(this, "File attached: " + item.getDisplayName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> projectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Project updatedProject = (Project) result.getData().getSerializableExtra("updated_project");
                    if (updatedProject != null) {
                        updateProjectById(updatedProject);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize with default sample project
        if (projectsList.isEmpty()) {
            projectsList.add(SampleDataProvider.createInitialSampleProject());
        }

        bindViews();
        setupListAndSearch();
        setupNewProjectButton();
    }

    private void bindViews() {
        listViewProjects = findViewById(R.id.listViewProjects);
        emptyProjectsView = findViewById(R.id.emptyProjectsView);
        textProjectsCount = findViewById(R.id.textProjectsCount);
        editSearchProjects = findViewById(R.id.editSearchProjects);
        buttonClearSearch = findViewById(R.id.buttonClearSearch);
    }

    private void setupListAndSearch() {
        adapter = new ProjectAdapter(this, projectsList, project -> {
            Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
            intent.putExtra("project", project);
            projectLauncher.launch(intent);
        });

        listViewProjects.setAdapter(adapter);
        listViewProjects.setEmptyView(emptyProjectsView);
        updateProjectsCount();

        editSearchProjects.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                buttonClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonClearSearch.setOnClickListener(v -> editSearchProjects.setText(""));
    }

    private void setupNewProjectButton() {
        Button buttonNewProject = findViewById(R.id.buttonNewProject);
        buttonNewProject.setOnClickListener(v -> showNewProjectDialog());
    }

    private void showNewProjectDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_project, null);
        EditText editName = dialogView.findViewById(R.id.editDialogProjectName);
        EditText editTag = dialogView.findViewById(R.id.editDialogProjectTag);
        EditText editDescription = dialogView.findViewById(R.id.editDialogProjectDescription);
        Button buttonSelectAttachment = dialogView.findViewById(R.id.buttonSelectAttachment);
        TextView textSelectedAttachment = dialogView.findViewById(R.id.textSelectedAttachment);
        ImageView buttonClearAttachment = dialogView.findViewById(R.id.buttonClearAttachment);
        Button buttonCancel = dialogView.findViewById(R.id.buttonNewProjectCancel);
        Button buttonAdd = dialogView.findViewById(R.id.buttonNewProjectAdd);

        final CatalogItem[] selectedItem = new CatalogItem[1];
        pendingDeviceItem = null;
        activeTextSelectedAttachment = textSelectedAttachment;
        activeButtonClearAttachment = buttonClearAttachment;

        buttonSelectAttachment.setOnClickListener(v -> {
            List<CatalogItem> items = CatalogRepository.getAllItems();
            AttachmentPickerHelper.showPicker(
                    this,
                    items,
                    () -> filePickerLauncher.launch("*/*"),
                    item -> {
                        selectedItem[0] = item;
                        textSelectedAttachment.setText(item.getDisplayName() + " (" + item.getType().getDisplayName() + ")");
                        buttonClearAttachment.setVisibility(View.VISIBLE);
                    }
            );
        });

        buttonClearAttachment.setOnClickListener(v -> {
            selectedItem[0] = null;
            pendingDeviceItem = null;
            textSelectedAttachment.setText("None selected");
            buttonClearAttachment.setVisibility(View.GONE);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String tag = editTag.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            boolean valid = true;

            if (name.isEmpty() || name.length() > 60) {
                editName.setError("Name is required (1–60 chars)");
                valid = false;
            }

            if (tag.isEmpty() || tag.length() > 20) {
                editTag.setError("Tag is required (1–20 chars)");
                valid = false;
            }

            if (description.isEmpty() || description.length() > 500) {
                editDescription.setError("Description is required (1–500 chars)");
                valid = false;
            }

            if (!valid) return;

            // Create project with 0/0 tasks, 0%, 0 comments
            Project newProject = new Project(tag, name, description);
            CatalogItem itemToAttach = selectedItem[0] != null ? selectedItem[0] : pendingDeviceItem;
            if (itemToAttach != null) {
                newProject.addAttachmentLink(new AttachmentLink(
                        itemToAttach.getId(),
                        itemToAttach.getDrawableResId()
                ));
            }

            projectsList.add(newProject);
            adapter.updateData(projectsList);
            updateProjectsCount();
            dialog.dismiss();
            Toast.makeText(this, "Project created successfully", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void updateProjectById(Project updatedProject) {
        if (updatedProject == null) return;
        boolean found = false;
        for (int i = 0; i < projectsList.size(); i++) {
            if (projectsList.get(i).getId().equals(updatedProject.getId())) {
                projectsList.set(i, updatedProject);
                found = true;
                break;
            }
        }
        if (!found) {
            projectsList.add(updatedProject);
        }
        adapter.updateData(projectsList);
        updateProjectsCount();
    }

    private void updateProjectsCount() {
        int count = projectsList.size();
        textProjectsCount.setText(count + (count == 1 ? " Project" : " Projects"));
    }
}
