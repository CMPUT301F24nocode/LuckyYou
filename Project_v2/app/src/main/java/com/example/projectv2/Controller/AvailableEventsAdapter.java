package com.example.projectv2.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.example.projectv2.View.EventLandingPageOrganizerActivity;
import com.example.projectv2.View.EventLandingPageUserActivity;

import java.util.List;

public class AvailableEventsAdapter extends RecyclerView.Adapter<AvailableEventsAdapter.ViewHolder> {

    private final List<Event> eventList;
    private final Context context;

    public AvailableEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_available_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");
        holder.eventDescription.setText(event.getDetail());

        // Set OnClickListener to navigate to EventLandingPageOrganizerActivity with event details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventLandingPageUserActivity.class);
            intent.putExtra("name", event.getName());
            intent.putExtra("details", event.getDetail());
            intent.putExtra("rules", event.getRules());
            intent.putExtra("deadline", event.getDeadline());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("price", event.getTicketPrice());
            intent.putExtra("eventID", event.getEventID());
            intent.putExtra("owner", event.getOwner());
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

    // Updates the event list and notifies the adapter
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice, eventDescription;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.available_event_name_text);
            eventDate = view.findViewById(R.id.available_event_date_text);
            eventPrice = view.findViewById(R.id.available_event_price_text);
            eventDescription = view.findViewById(R.id.available_event_description_text);
        }
    }
}