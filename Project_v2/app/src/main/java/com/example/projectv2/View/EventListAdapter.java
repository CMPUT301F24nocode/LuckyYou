package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.example.projectv2.Model.Event;
import com.example.projectv2.R;


import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the event item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_object, parent, false);
        }

        // Initialize ImageView and set image
        ImageView backgroundImageView = convertView.findViewById(R.id.backgroundImage);
        if (event != null && event.getImageUri() != null) {
            backgroundImageView.setImageURI(event.getImageUri());
        } else {
            backgroundImageView.setImageResource(R.drawable.placeholder_event); // Default image if URI is null
        }

        // Initialize TextViews and populate with data
        TextView eventNameTextView = convertView.findViewById(R.id.textView12);
        TextView eventDeadlineTextView = convertView.findViewById(R.id.textView13);
        TextView eventDetailTextView = convertView.findViewById(R.id.textView14);
        TextView eventPriceTextView = convertView.findViewById(R.id.textView15);

        if (event != null) {
            eventNameTextView.setText(event.getName());
            eventDeadlineTextView.setText(event.getDeadline());
            eventDetailTextView.setText(event.getDetail());

            // Display ticket price or "Free" if no price specified
            if (event.getTicketPrice() != null && !event.getTicketPrice().isEmpty() && !event.getTicketPrice().equals("0")) {
                eventPriceTextView.setText("$" + event.getTicketPrice());
            } else {
                eventPriceTextView.setText("Free");
            }

            // Log information for debugging
            Log.d("EventListAdapter", "Event displayed: " + event.getName());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

