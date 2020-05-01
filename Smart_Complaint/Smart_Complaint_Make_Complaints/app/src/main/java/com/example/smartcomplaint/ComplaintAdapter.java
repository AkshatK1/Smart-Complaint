package com.example.smartcomplaint;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class ComplaintAdapter extends ArrayAdapter<SmartComplaint> {
    public ComplaintAdapter(Context context, int resource, List<SmartComplaint> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_complaint, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView receiverTextView = (TextView) convertView.findViewById(R.id.receiver_label);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        SmartComplaint message = getItem(position);

        boolean isPhoto = Objects.requireNonNull(message).getPhotoUrl() != null;
        if (isPhoto) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(message.getTitle());
            receiverTextView.setVisibility(View.VISIBLE);
            receiverTextView.setText(message.getAdmin());
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.getText());
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(message.getTitle());
            receiverTextView.setVisibility(View.VISIBLE);
            receiverTextView.setText(message.getAdmin());
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }
        authorTextView.setText(message.getName());

        return convertView;
    }
}