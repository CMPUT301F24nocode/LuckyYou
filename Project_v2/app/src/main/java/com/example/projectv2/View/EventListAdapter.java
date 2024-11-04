package com.example.projectv2.View;

import android.content.Context;
import android.content.Intent;
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

        // Load image directly using URI with a try-catch block for safety
        try {
            if (event.getImageUri() != null) {
                Uri imageUri = event.getImageUri();
                holder.backgroundImageView.setImageURI(imageUri);

                // Check if the image failed to load and set a default image
                if (holder.backgroundImageView.getDrawable() == null) {
                    holder.backgroundImageView.setImageResource(R.drawable.placeholder_event);
                }
            } else {
                holder.backgroundImageView.setImageResource(R.drawable.placeholder_event);
            }
        } catch (Exception e) {
            holder.backgroundImageView.setImageResource(R.drawable.placeholder_event);
            e.printStackTrace();
        }

        holder.eventNameTextView.setText(event.getName());
        holder.eventDateTextView.setText(event.getDeadline());
        holder.eventDetailTextView.setText(event.getDetail());
        holder.eventPriceTextView.setText(
                (event.getTicketPrice() != null && !event.getTicketPrice().isEmpty() && !event.getTicketPrice().equals("0"))
                        ? "$" + event.getTicketPrice()
                        : "Free"
        );

        // Set click listener to open EventDetailsActivity with consistent keys
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("name", event.getName());
            intent.putExtra("details", event.getDetail());
            intent.putExtra("rules", event.getRules());
            intent.putExtra("deadline", event.getDeadline());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("price", event.getTicketPrice());
            if (event.getImageUri() != null) {
                intent.putExtra("imageUri", event.getImageUri().toString());
            }
            context.startActivity(intent);
        });
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