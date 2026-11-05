package com.example.firstapp;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilesFragment extends Fragment implements AttachmentAdapter.AttachmentActionListener {

    private enum ChipSelection {
        ALL, IMAGES, TEXT
    }

    private EditText editSearchAttachments;
    private ImageView buttonClearAttachmentSearch;
    private TextView chipFilterAll;
    private TextView chipFilterImages;
    private TextView chipFilterText;
    private TextView textFilesShowingCount;

    private ListView listViewAttachments;
    private View layoutEmptyNoAttachments;
    private View layoutEmptyNoFileMatch;
    private Button buttonEmptyAddAttachment;
    private Button buttonEmptyResetFiles;
    private Button buttonFloatingAddAttachment;

    private AttachmentAdapter attachmentAdapter;
    private final List<AttachmentLink> displayedLinks = new ArrayList<>();

    private String currentSearch = "";
    private ChipSelection currentChip = ChipSelection.ALL;

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && isAdded()) {
                    CatalogItem item = AttachmentPickerHelper.processPickedUri(requireContext(), uri);
                    if (item != null) {
                        ProjectHostCallback host = (ProjectHostCallback) getActivity();
                        if (host != null && host.getProject() != null) {
                            host.getProject().addAttachmentLink(new AttachmentLink(item.getId(), item.getDrawableResId()));
                            host.onProjectUpdated();
                            applyFilters();
                            Toast.makeText(requireContext(), "File attached: " + item.getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    public static FilesFragment newInstance(String projectId) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString("project_id", projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        bindViews(view);
        setupAdapter();
        setupListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyFilters();
    }

    private void bindViews(View v) {
        editSearchAttachments = v.findViewById(R.id.editSearchAttachments);
        buttonClearAttachmentSearch = v.findViewById(R.id.buttonClearAttachmentSearch);
        chipFilterAll = v.findViewById(R.id.chipFilterAll);
        chipFilterImages = v.findViewById(R.id.chipFilterImages);
        chipFilterText = v.findViewById(R.id.chipFilterText);
        textFilesShowingCount = v.findViewById(R.id.textFilesShowingCount);

        listViewAttachments = v.findViewById(R.id.listViewAttachments);
        layoutEmptyNoAttachments = v.findViewById(R.id.layoutEmptyNoAttachments);
        layoutEmptyNoFileMatch = v.findViewById(R.id.layoutEmptyNoFileMatch);
        buttonEmptyAddAttachment = v.findViewById(R.id.buttonEmptyAddAttachment);
        buttonEmptyResetFiles = v.findViewById(R.id.buttonEmptyResetFiles);
        buttonFloatingAddAttachment = v.findViewById(R.id.buttonFloatingAddAttachment);
    }

    private void setupAdapter() {
        attachmentAdapter = new AttachmentAdapter(requireContext(), displayedLinks, this);
        listViewAttachments.setAdapter(attachmentAdapter);
    }

    private void setupListeners() {
        // Search Listener
        editSearchAttachments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().trim();
                buttonClearAttachmentSearch.setVisibility(currentSearch.isEmpty() ? View.GONE : View.VISIBLE);
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonClearAttachmentSearch.setOnClickListener(v -> editSearchAttachments.setText(""));

        // Chips
        chipFilterAll.setOnClickListener(v -> {
            currentChip = ChipSelection.ALL;
            updateChipStyles();
            applyFilters();
        });

        chipFilterImages.setOnClickListener(v -> {
            currentChip = ChipSelection.IMAGES;
            updateChipStyles();
            applyFilters();
        });

        chipFilterText.setOnClickListener(v -> {
            currentChip = ChipSelection.TEXT;
            updateChipStyles();
            applyFilters();
        });

        // Add Attachment Buttons
        buttonFloatingAddAttachment.setOnClickListener(v -> openAddAttachmentDialog());
        buttonEmptyAddAttachment.setOnClickListener(v -> openAddAttachmentDialog());

        // Reset Files Filter
        buttonEmptyResetFiles.setOnClickListener(v -> resetFilters());
    }

    private void resetFilters() {
        currentSearch = "";
        editSearchAttachments.setText("");
        currentChip = ChipSelection.ALL;
        updateChipStyles();
        applyFilters();
    }

    private void updateChipStyles() {
        if (!isAdded()) return;

        chipFilterAll.setBackgroundResource(currentChip == ChipSelection.ALL ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterAll.setTextColor(currentChip == ChipSelection.ALL ? Color.WHITE : Color.parseColor("#555555"));

        chipFilterImages.setBackgroundResource(currentChip == ChipSelection.IMAGES ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterImages.setTextColor(currentChip == ChipSelection.IMAGES ? Color.WHITE : Color.parseColor("#555555"));

        chipFilterText.setBackgroundResource(currentChip == ChipSelection.TEXT ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterText.setTextColor(currentChip == ChipSelection.TEXT ? Color.WHITE : Color.parseColor("#555555"));
    }

    public void applyFilters() {
        if (!isAdded()) return;
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        List<AttachmentLink> allLinks = project.getAttachmentLinks();
        int totalLinks = allLinks.size();

        // Compute total collection counts for chips
        int totalImages = 0;
        int totalText = 0;
        for (AttachmentLink link : allLinks) {
            CatalogItem item = CatalogRepository.getItemById(link.getCatalogId());
            if (item != null) {
                if (item.getType() == AttachmentType.IMAGE) {
                    totalImages++;
                } else if (item.getType() == AttachmentType.TEXT) {
                    totalText++;
                }
            }
        }

        chipFilterAll.setText("All " + totalLinks);
        chipFilterImages.setText("Images " + totalImages);
        chipFilterText.setText("Text " + totalText);

        // Filter preserving link-insertion order
        displayedLinks.clear();
        for (AttachmentLink link : allLinks) {
            CatalogItem item = CatalogRepository.getItemById(link.getCatalogId());
            if (item == null) continue;

            // Type chip filter
            if (currentChip == ChipSelection.IMAGES && item.getType() != AttachmentType.IMAGE) {
                continue;
            }
            if (currentChip == ChipSelection.TEXT && item.getType() == AttachmentType.IMAGE) {
                continue;
            }

            // Search filter on display name (case-insensitive)
            if (!currentSearch.isEmpty()) {
                String name = item.getDisplayName().toLowerCase(Locale.getDefault());
                if (!name.contains(currentSearch.toLowerCase(Locale.getDefault()))) {
                    continue;
                }
            }

            displayedLinks.add(link);
        }

        attachmentAdapter.notifyDataSetChanged();

        // Update "Showing X of Y" line
        textFilesShowingCount.setText("Showing " + displayedLinks.size() + " of " + totalLinks + " attachments");

        // Handle empty states
        if (totalLinks == 0) {
            listViewAttachments.setVisibility(View.GONE);
            layoutEmptyNoAttachments.setVisibility(View.VISIBLE);
            layoutEmptyNoFileMatch.setVisibility(View.GONE);
            buttonFloatingAddAttachment.setVisibility(View.GONE);
        } else if (displayedLinks.isEmpty()) {
            listViewAttachments.setVisibility(View.GONE);
            layoutEmptyNoAttachments.setVisibility(View.GONE);
            layoutEmptyNoFileMatch.setVisibility(View.VISIBLE);
            buttonFloatingAddAttachment.setVisibility(View.VISIBLE);
        } else {
            listViewAttachments.setVisibility(View.VISIBLE);
            layoutEmptyNoAttachments.setVisibility(View.GONE);
            layoutEmptyNoFileMatch.setVisibility(View.GONE);
            buttonFloatingAddAttachment.setVisibility(View.VISIBLE);
        }
    }

    private void openAddAttachmentDialog() {
        ProjectHostCallback host = (ProjectHostCallback) getActivity();
        if (host == null) return;
        Project project = host.getProject();
        if (project == null) return;

        // Find unlinked catalog items
        List<CatalogItem> allCatalog = CatalogRepository.getAllItems();
        List<CatalogItem> unlinked = new ArrayList<>();
        for (CatalogItem ci : allCatalog) {
            if (!project.hasAttachment(ci.getId())) {
                unlinked.add(ci);
            }
        }

        AttachmentPickerHelper.showPicker(
                requireContext(),
                unlinked,
                () -> filePickerLauncher.launch("*/*"),
                chosen -> {
                    project.addAttachmentLink(new AttachmentLink(chosen.getId(), chosen.getDrawableResId()));
                    host.onProjectUpdated();
                    applyFilters();
                    Toast.makeText(requireContext(), "Attachment added", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // AttachmentAdapter.AttachmentActionListener
    @Override
    public void onPreview(CatalogItem item) {
        if (item == null || !isAdded()) return;
        // Open file using the phone's suitable app
        AttachmentPickerHelper.openFileWithSuitableApp(requireContext(), item);
    }

    @Override
    public void onRemove(AttachmentLink link, CatalogItem item) {
        if (link == null || item == null) return;

        View confirmView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete_attachment, null);
        TextView targetNameView = confirmView.findViewById(R.id.textTargetAttachmentName);
        EditText editConfirm = confirmView.findViewById(R.id.editConfirmAttachmentName);
        Button buttonCancel = confirmView.findViewById(R.id.buttonConfirmDeleteCancel);
        Button buttonRemove = confirmView.findViewById(R.id.buttonConfirmDeleteRemove);

        targetNameView.setText("\"" + item.getDisplayName() + "\"");

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(confirmView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonRemove.setOnClickListener(v -> {
            String typed = editConfirm.getText().toString().trim();
            if (typed.equals(item.getDisplayName().trim())) {
                ProjectHostCallback host = (ProjectHostCallback) getActivity();
                if (host != null) {
                    host.getProject().removeAttachmentLink(link.getId());
                    host.onProjectUpdated();
                    applyFilters();
                    Toast.makeText(requireContext(), "Attachment removed", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                editConfirm.setError("Name does not match exact attachment name");
            }
        });

        dialog.show();
    }
}
