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
import com.example.projectv2.View.AdminEventOverlayDialog;

import java.util.List;

/**
 * Adapter class for displaying a list of events in a RecyclerView for administrative purposes.
 * Each event is represented as an item in the RecyclerView.
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {

    private final Context context;
    private final List<Event> eventList;

    /**
     * Constructor for initializing the AdminEventsAdapter with the context and event list.
     *
     * @param context   the context in which the RecyclerView is being used
     * @param eventList the list of events to display
     */
    public AdminEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    /**
     * Inflates the layout for each RecyclerView item and creates a ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new ViewHolder instance for the inflated item layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_event_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data of an event to the corresponding views in the ViewHolder.
     *
     * @param holder   the ViewHolder for the current item
     * @param position the position of the item in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Populate the views with event data
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventDescription.setText(event.getDetail());
        holder.eventOrganizer.setText("Organizer: " + event.getOwner());

        // Set onClickListener for navigating to AdminEventOverlayActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminEventOverlayDialog.class);
            intent.putExtra("name", event.getName());
            intent.putExtra("eventID", event.getEventID());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of events in the data list.
     *
     * @return the size of the event list
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class that holds the views for displaying an event's details in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice, eventDescription, eventOrganizer;

        /**
         * Constructor for initializing the ViewHolder with the corresponding views.
         *
         * @param view the item view for the ViewHolder
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.event_name);
            eventDate = view.findViewById(R.id.event_date);
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
