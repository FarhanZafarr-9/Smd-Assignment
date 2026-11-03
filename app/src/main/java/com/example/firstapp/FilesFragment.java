package com.example.firstapp;

import android.graphics.Color;
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
            if (currentChip == ChipSelection.TEXT && item.getType() != AttachmentType.TEXT) {
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

        if (unlinked.isEmpty()) {
            Toast.makeText(requireContext(), "All catalog items have already been linked", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] itemLabels = new String[unlinked.size()];
        for (int i = 0; i < unlinked.size(); i++) {
            CatalogItem ci = unlinked.get(i);
            itemLabels[i] = ci.getDisplayName() + " (" + ci.getType().getDisplayName() + ")";
        }

        final int[] selectedIndex = {-1};

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Attachment from Catalog")
                .setSingleChoiceItems(itemLabels, -1, (d, which) -> {
                    selectedIndex[0] = which;
                    AlertDialog ad = (AlertDialog) d;
                    Button addBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                    if (addBtn != null) {
                        addBtn.setEnabled(true);
                    }
                })
                .setPositiveButton("Add", (d, which) -> {
                    if (selectedIndex[0] >= 0 && selectedIndex[0] < unlinked.size()) {
                        CatalogItem chosen = unlinked.get(selectedIndex[0]);
                        project.addAttachmentLink(new AttachmentLink(chosen.getId(), chosen.getDrawableResId()));
                        host.onProjectUpdated();
                        applyFilters();
                        Toast.makeText(requireContext(), "Attachment added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button addBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (addBtn != null) {
                // Add stays disabled until an eligible item is selected
                addBtn.setEnabled(false);
            }
        });

        dialog.show();
    }

    // AttachmentAdapter.AttachmentActionListener
    @Override
    public void onPreview(CatalogItem item) {
        if (item == null) return;

        View previewView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_attachment_preview, null);
        TextView titleView = previewView.findViewById(R.id.textPreviewTitle);
        TextView typeView = previewView.findViewById(R.id.textPreviewType);
        ImageView imageView = previewView.findViewById(R.id.imagePreviewContent);
        View scrollView = previewView.findViewById(R.id.scrollTextPreview);
        TextView textContentView = previewView.findViewById(R.id.textPreviewContent);

        titleView.setText(item.getDisplayName());
        typeView.setText(item.getType().getDisplayName());

        if (item.getType() == AttachmentType.IMAGE) {
            imageView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            if (item.getDrawableResId() != 0) {
                imageView.setImageResource(item.getDrawableResId());
            } else {
                imageView.setImageResource(R.drawable.ic_image_file);
            }
        } else {
            imageView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            textContentView.setText(item.getTextContent() != null ? item.getTextContent() : "No content available.");
        }

        new AlertDialog.Builder(requireContext())
                .setView(previewView)
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void onRemove(AttachmentLink link, CatalogItem item) {
        if (link == null || item == null) return;

        View confirmView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete_attachment, null);
        TextView targetNameView = confirmView.findViewById(R.id.textTargetAttachmentName);
        EditText editConfirm = confirmView.findViewById(R.id.editConfirmAttachmentName);

        targetNameView.setText("\"" + item.getDisplayName() + "\"");

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(confirmView)
                .setPositiveButton("Remove", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button removeBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            removeBtn.setOnClickListener(v -> {
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
        });

        dialog.show();
    }
}
