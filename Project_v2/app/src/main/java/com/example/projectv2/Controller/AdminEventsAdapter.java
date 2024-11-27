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
import com.example.projectv2.View.AdminEventOverlayActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {

    private final Context context;
    private final List<Event> eventList;

    public AdminEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Populate the views with event data
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");
        holder.eventDescription.setText(event.getDetail()); // Assuming 'detail' represents the description
        holder.eventOrganizer.setText("Organizer: " + event.getOwner()); // Assuming 'owner' is the organizer name

        // Set onClickListener for navigating to AdminEventOverlayActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminEventOverlayActivity.class);
            intent.putExtra("name",event.getName());
            intent.putExtra("eventID", event.getEventID());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice, eventDescription, eventOrganizer;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.event_name);
            eventDate = view.findViewById(R.id.event_date);
            eventPrice = view.findViewById(R.id.event_price);
            eventDescription = view.findViewById(R.id.event_description);
            eventOrganizer = view.findViewById(R.id.event_organizer);
        }
    }

    /**
     * Updates the event list in the adapter and refreshes the RecyclerView.
     *
     * @param newEvents the new list of events to display
     */
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }
}
