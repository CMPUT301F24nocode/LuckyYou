package com.example.projectv2.View;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.AvailableEventsAdapter;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.example.projectv2.View.CreateEventActivity;

import java.util.ArrayList;
import java.util.List;

public class AvailableEventsFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private EventController eventController;
    private AvailableEventsAdapter adapter;

    public AvailableEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_events, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAvailableEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableEventsAdapter(getContext(), new ArrayList<>()); // Pass context
        recyclerView.setAdapter(adapter);

        // Initialize EventController for Firestore operations
        eventController = new EventController(getActivity());

        // Fetch events from Firebase and update display
        fetchEventsFromFirebase();

        return view;
    }

    private void fetchEventsFromFirebase() {
        Log.d("AvailableEventsFragment", "Starting Firebase fetch...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("AvailableEventsFragment", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events);
            }

            @Override
            public void onEventCreated(String eventId) {
                Log.d("AvailableEventsFragment", "Event created with ID: " + eventId);
            }

            @Override
            public void onError(Exception e) {
                Log.e("AvailableEventsFragment", "Error fetching events", e);
            }
        });
    }

    public void startCreateEventActivity() {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String detail = data.getStringExtra("detail");
            String rules = data.getStringExtra("rules");
            String deadline = data.getStringExtra("deadline");
            String facility = data.getStringExtra("facility");
            String attendees = data.getStringExtra("attendees");
            String entrants = data.getStringExtra("entrants");
            String startDate = data.getStringExtra("startDate");
            String ticketPrice = data.getStringExtra("ticketPrice");
            boolean geolocationEnabled = data.getBooleanExtra("geolocationEnabled", false);
            boolean notificationsEnabled = data.getBooleanExtra("notificationsEnabled", false);
            Uri imageUri = data.getStringExtra("imageUri") != null ? Uri.parse(data.getStringExtra("imageUri")) : null;

            Toast.makeText(getActivity(), "Event Created: " + name, Toast.LENGTH_SHORT).show();

            eventController.createEvent(
                    name, detail, rules, deadline, attendees, entrants, startDate, ticketPrice, geolocationEnabled, notificationsEnabled, imageUri, facility,
                    new EventController.EventCallback() {
                        @Override
                        public void onEventListLoaded(ArrayList<Event> events) {
                            adapter.updateEventList(events);
                        }

                        @Override
                        public void onEventCreated(String eventId) {
                            Log.d("AvailableEventsFragment", "Event created successfully with ID: " + eventId);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("AvailableEventsFragment", "Error adding event", e);
                        }
                    }
            );


        }
    }
}