package com.example.firstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    public interface AttachmentActionListener {
        void onPreview(CatalogItem item);
        void onRemove(AttachmentLink link, CatalogItem item, int position);
    }

    private final List<AttachmentLink> links;
    private final AttachmentActionListener listener;

    public AttachmentAdapter(List<AttachmentLink> links, AttachmentActionListener listener) {
        this.links = links != null ? links : new ArrayList<>();
        this.listener = listener;
    }

    public AttachmentLink getItem(int position) {
        if (position >= 0 && position < links.size()) {
            return links.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment, parent, false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        AttachmentLink link = links.get(position);
        holder.bind(link, listener);
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public void updateLinks(List<AttachmentLink> newLinks) {
        this.links.clear();
        if (newLinks != null) {
            this.links.addAll(newLinks);
        }
        notifyDataSetChanged();
    }

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final TextView name;
        private final TextView type;
        private final TextView buttonPreview;
        private final ImageView buttonRemove;

        AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageAttachmentThumbnail);
            name = itemView.findViewById(R.id.textAttachmentName);
            type = itemView.findViewById(R.id.textAttachmentType);
            buttonPreview = itemView.findViewById(R.id.buttonPreviewAttachment);
            buttonRemove = itemView.findViewById(R.id.buttonRemoveAttachment);
        }

        void bind(AttachmentLink link, AttachmentActionListener listener) {
            if (link == null) return;
            CatalogItem item = CatalogRepository.getItemById(link.getCatalogId());
            if (item != null) {
                name.setText(item.getDisplayName());
                type.setText(item.getType().getDisplayName());

                if (item.getType() == AttachmentType.PDF) {
                    thumbnail.setImageResource(R.drawable.ic_pdf_file);
                } else if (item.getType() == AttachmentType.IMAGE) {
                    if (item.getDrawableResId() != 0) {
                        thumbnail.setImageResource(item.getDrawableResId());
                    } else {
                        thumbnail.setImageResource(R.drawable.ic_image_file);
                    }
                } else {
                    thumbnail.setImageResource(R.drawable.ic_text_file);
                }

                buttonPreview.setOnClickListener(v -> {
                    if (listener != null) listener.onPreview(item);
                });

                buttonRemove.setOnClickListener(v -> {
                    if (listener != null) listener.onRemove(link, item, getAdapterPosition());
                });

                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onPreview(item);
                });
            } else {
                name.setText(link.getCatalogId());
                type.setText("Unknown");
                thumbnail.setImageResource(R.drawable.attachment_icon);
            }
        }
    }
}
