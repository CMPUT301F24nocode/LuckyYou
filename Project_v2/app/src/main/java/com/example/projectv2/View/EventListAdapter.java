package com.example.projectv2.View;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.eventList = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homescreen_available_events_list_object, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set image
        if (event.getImageUri() != null) {
            holder.backgroundImageView.setImageURI(event.getImageUri());
        } else {
            holder.backgroundImageView.setImageResource(R.drawable.placeholder_event); // Default image
        }

        // Set text details
        holder.eventNameTextView.setText(event.getName());
        holder.eventDateTextView.setText(event.getDeadline());
        holder.eventDetailTextView.setText(event.getDetail());

        // Display ticket price or "Free" if not specified
        if (event.getTicketPrice() != null && !event.getTicketPrice().isEmpty() && !event.getTicketPrice().equals("0")) {
            holder.eventPriceTextView.setText("$" + event.getTicketPrice());
        } else {
            holder.eventPriceTextView.setText("Free");
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView backgroundImageView;
        TextView eventNameTextView;
        TextView eventDateTextView;
        TextView eventDetailTextView;
        TextView eventPriceTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImageView = itemView.findViewById(R.id.backgroundImage);
            eventNameTextView = itemView.findViewById(R.id.available_event_name_text);
            eventDateTextView = itemView.findViewById(R.id.available_event_date_text);
            eventDetailTextView = itemView.findViewById(R.id.available_event_description_text);
            eventPriceTextView = itemView.findViewById(R.id.available_event_price_text);
        }
    }
}
