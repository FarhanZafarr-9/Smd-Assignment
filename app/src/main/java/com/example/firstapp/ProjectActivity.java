package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProjectActivity extends AppCompatActivity implements ProjectHostCallback {

    private Project project;
    private ProjectPagerAdapter pagerAdapter;

    private TextView textProjectTag;
    private TextView textProjectTitle;
    private ImageView buttonBack;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabLayoutMediator tabMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        project = (Project) getIntent().getSerializableExtra("project");
        if (project == null) {
            finish();
            return;
        }

        bindViews();
        setupHeader();
        setupPagerAndTabs();
        setupBackNavigation();
    }

    private void bindViews() {
        textProjectTag = findViewById(R.id.textProjectTag);
        textProjectTitle = findViewById(R.id.textProjectTitle);
        buttonBack = findViewById(R.id.buttonBack);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupHeader() {
        if (project != null) {
            textProjectTag.setText(project.getTag());
            textProjectTitle.setText(project.getName());
        }
    }

    private void setupPagerAndTabs() {
        pagerAdapter = new ProjectPagerAdapter(this, project.getId());
        viewPager.setAdapter(pagerAdapter);

        // Keep all 4 tabs alive in memory so search, filters, and sort are preserved during the same visit
        viewPager.setOffscreenPageLimit(3);

        tabMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Tasks (" + project.getTotalTasks() + ")");
                    break;
                case 2:
                    tab.setText("Files (" + project.getAttachmentCount() + ")");
                    break;
                case 3:
                default:
                    tab.setText("Discussion (" + project.getCommentCount() + ")");
                    break;
            }
        });
        tabMediator.attach();
    }

    public void updateTabTitles() {
        if (tabLayout != null && project != null) {
            TabLayout.Tab tab0 = tabLayout.getTabAt(0);
            if (tab0 != null) tab0.setText("Overview");

            TabLayout.Tab tab1 = tabLayout.getTabAt(1);
            if (tab1 != null) tab1.setText("Tasks (" + project.getTotalTasks() + ")");

            TabLayout.Tab tab2 = tabLayout.getTabAt(2);
            if (tab2 != null) tab2.setText("Files (" + project.getAttachmentCount() + ")");

            TabLayout.Tab tab3 = tabLayout.getTabAt(3);
            if (tab3 != null) tab3.setText("Discussion (" + project.getCommentCount() + ")");
        }
        setupHeader();
    }

    private void setupBackNavigation() {
        buttonBack.setOnClickListener(v -> returnWithResult());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnWithResult();
            }
        });
    }

    private void returnWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_project", project);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // ProjectHostCallback Implementation
    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void onProjectUpdated() {
        updateTabTitles();
    }

    @Override
    public void selectTab(int position) {
        if (viewPager != null && position >= 0 && position < 4) {
            viewPager.setCurrentItem(position, true);
        }
    }

    @Override
    public void selectTasksWithStatusFilter(TaskStatus status) {
        selectTab(1); // Select Tasks tab
        // TasksFragment will apply this filter
    }

    @Override
    public void selectTasksWithOverdueFilter() {
        selectTab(1); // Select Tasks tab
        // TasksFragment will apply this filter
    }

    @Override
    public void openAddTaskDialog() {
        // Implemented in TasksFragment or OverviewFragment
    }

    @Override
    public void openEditProjectDialog() {
        // Implemented in OverviewFragment
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabMediator != null) {
            tabMediator.detach();
        }
    }
}
