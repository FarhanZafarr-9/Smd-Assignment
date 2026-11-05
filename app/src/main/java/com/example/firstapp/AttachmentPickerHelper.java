package com.example.firstapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttachmentPickerHelper {

    public interface OnAttachmentPickedListener {
        void onAttachmentPicked(CatalogItem item);
    }

    public static void showPicker(
            Context context,
            List<CatalogItem> availableItems,
            Runnable onPickFromDeviceRequested,
            OnAttachmentPickedListener listener
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_catalog_picker, null);

        Button buttonPickFromDevice = dialogView.findViewById(R.id.buttonPickFromDevice);
        ListView listViewCatalogItems = dialogView.findViewById(R.id.listViewCatalogItems);
        Button buttonCatalogCancel = dialogView.findViewById(R.id.buttonCatalogCancel);
        Button buttonCatalogAdd = dialogView.findViewById(R.id.buttonCatalogAdd);

        final int[] selectedPosition = {-1};
        final CatalogChoiceAdapter choiceAdapter = new CatalogChoiceAdapter(context, availableItems, selectedPosition);
        listViewCatalogItems.setAdapter(choiceAdapter);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        listViewCatalogItems.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition[0] = position;
            choiceAdapter.notifyDataSetChanged();
            buttonCatalogAdd.setEnabled(true);
        });

        buttonPickFromDevice.setOnClickListener(v -> {
            dialog.dismiss();
            if (onPickFromDeviceRequested != null) {
                onPickFromDeviceRequested.run();
            }
        });

        buttonCatalogCancel.setOnClickListener(v -> dialog.dismiss());

        buttonCatalogAdd.setOnClickListener(v -> {
            if (selectedPosition[0] >= 0 && selectedPosition[0] < availableItems.size()) {
                CatalogItem chosen = availableItems.get(selectedPosition[0]);
                if (listener != null) {
                    listener.onAttachmentPicked(chosen);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static CatalogItem processPickedUri(Context context, Uri uri) {
        if (uri == null) return null;

        String displayName = "Picked_File_" + System.currentTimeMillis();
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception ignored) {}

        String mimeType = context.getContentResolver().getType(uri);
        boolean isImage = (mimeType != null && mimeType.startsWith("image/"))
                || displayName.toLowerCase().endsWith(".png")
                || displayName.toLowerCase().endsWith(".jpg")
                || displayName.toLowerCase().endsWith(".jpeg");

        AttachmentType type = isImage ? AttachmentType.IMAGE : AttachmentType.TEXT;

        String textContent = null;
        if (!isImage) {
            try (InputStream is = context.getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                int linesRead = 0;
                while ((line = reader.readLine()) != null && linesRead < 100) {
                    sb.append(line).append("\n");
                    linesRead++;
                }
                textContent = sb.toString();
            } catch (Exception e) {
                textContent = "File content from device: " + displayName;
            }
        }

        String newId = "dev_" + UUID.randomUUID().toString().substring(0, 8);
        CatalogItem newItem = new CatalogItem(
                newId,
                displayName,
                type,
                isImage ? R.drawable.ic_image_file : 0,
                textContent,
                uri.toString()
        );

        CatalogRepository.addItem(newItem);
        return newItem;
    }

    private static class CatalogChoiceAdapter extends BaseAdapter {
        private final Context context;
        private final List<CatalogItem> items;
        private final int[] selectedPosition;
        private final LayoutInflater inflater;

        CatalogChoiceAdapter(Context context, List<CatalogItem> items, int[] selectedPosition) {
            this.context = context;
            this.items = items != null ? items : new ArrayList<>();
            this.selectedPosition = selectedPosition;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CatalogItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_catalog_choice, parent, false);
            }

            RadioButton radio = convertView.findViewById(R.id.radioCatalogChoice);
            ImageView icon = convertView.findViewById(R.id.imageCatalogIcon);
            TextView textName = convertView.findViewById(R.id.textCatalogDisplayName);
            TextView textType = convertView.findViewById(R.id.textCatalogType);

            CatalogItem item = getItem(position);
            if (item != null) {
                textName.setText(item.getDisplayName());
                textType.setText(item.getType().getDisplayName());

                if (item.getType() == AttachmentType.IMAGE) {
                    if (item.getDrawableResId() != 0) {
                        icon.setImageResource(item.getDrawableResId());
                    } else {
                        icon.setImageResource(R.drawable.ic_image_file);
                    }
                } else {
                    icon.setImageResource(R.drawable.ic_text_file);
                }

                radio.setChecked(position == selectedPosition[0]);
            }

            return convertView;
        }
    }
}
