package com.example.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttachmentAdapter extends BaseAdapter {

    public interface AttachmentActionListener {
        void onPreview(CatalogItem item);
        void onRemove(AttachmentLink link, CatalogItem item);
    }

    private final Context context;
    private final List<AttachmentLink> links;
    private final LayoutInflater inflater;
    private final AttachmentActionListener listener;

    public AttachmentAdapter(Context context, List<AttachmentLink> links, AttachmentActionListener listener) {
        this.context = context;
        this.links = links != null ? links : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public AttachmentLink getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateLinks(List<AttachmentLink> newLinks) {
        this.links.clear();
        if (newLinks != null) {
            this.links.addAll(newLinks);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_attachment, parent, false);
            holder = new ViewHolder();
            holder.thumbnail = convertView.findViewById(R.id.imageAttachmentThumbnail);
            holder.name = convertView.findViewById(R.id.textAttachmentName);
            holder.type = convertView.findViewById(R.id.textAttachmentType);
            holder.buttonPreview = convertView.findViewById(R.id.buttonPreviewAttachment);
            holder.buttonRemove = convertView.findViewById(R.id.buttonRemoveAttachment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AttachmentLink link = getItem(position);
        if (link != null) {
            CatalogItem item = CatalogRepository.getItemById(link.getCatalogId());
            if (item != null) {
                holder.name.setText(item.getDisplayName());
                holder.type.setText(item.getType().getDisplayName());

                if (item.getType() == AttachmentType.PDF) {
                    holder.thumbnail.setImageResource(R.drawable.ic_pdf_file);
                } else if (item.getType() == AttachmentType.IMAGE) {
                    if (item.getDrawableResId() != 0) {
                        holder.thumbnail.setImageResource(item.getDrawableResId());
                    } else {
                        holder.thumbnail.setImageResource(R.drawable.ic_image_file);
                    }
                } else {
                    holder.thumbnail.setImageResource(R.drawable.ic_text_file);
                }

                holder.buttonPreview.setOnClickListener(v -> {
                    if (listener != null) listener.onPreview(item);
                });

                holder.buttonRemove.setOnClickListener(v -> {
                    if (listener != null) listener.onRemove(link, item);
                });

                convertView.setOnClickListener(v -> {
                    if (listener != null) listener.onPreview(item);
                });
            } else {
                holder.name.setText(link.getCatalogId());
                holder.type.setText("Unknown");
                holder.thumbnail.setImageResource(R.drawable.attachment_icon);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView type;
        TextView buttonPreview;
        ImageView buttonRemove;
    }
}
