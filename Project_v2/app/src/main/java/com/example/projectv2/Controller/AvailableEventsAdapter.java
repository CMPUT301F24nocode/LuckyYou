package com.example.projectv2.Controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AvailableEventsAdapter extends RecyclerView.Adapter<AvailableEventsAdapter.ViewHolder> {

    private final List<Event> eventList; // List of Event objects

    public AvailableEventsAdapter(List<Event> eventList) {
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
        Event event = eventList.get(position); // Get the Event object
        holder.eventName.setText(event.getName()); // Set the event name

        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String eventId = event.getId(); // Get the event ID
                String userId = getCurrentUserId(); // Replace with your actual method to get user ID

                EventController eventController = new EventController(context);
                eventController.joinWaitingList(eventId, userId, new EventController.EventCallback() {
                    @Override
                    public void onEventListLoaded(List<Event> events) {
                        Toast.makeText(context, "Successfully joined the waiting list!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEventListLoaded(ArrayList<Event> events) {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Failed to join the waiting list. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("JoinWaitingList", "Error joining waiting list", e);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public Button joinButton;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.available_event_name_text); // Ensure the ID matches your layout
            joinButton = view.findViewById(R.id.join_button); // Ensure the ID matches your layout
        }
    }

    // Placeholder method to get current user ID. Replace with actual implementation.
    private String getCurrentUserId() {
        // Retrieve user ID from your app's user session
        return "dummyUserId"; // Replace with actual implementation
    }
}


