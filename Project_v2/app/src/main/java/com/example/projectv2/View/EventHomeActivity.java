package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class EventHomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private EventController eventController;
    private RecyclerView recyclerView;
    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homescreen);

        // Initialize RecyclerView for event display
        recyclerView = findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventListAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize EventController for Firestore operations
        eventController = new EventController(this);

        // Fetch events from Firestore and update display
        fetchEventsFromFirebase();

        // Set up FloatingActionButton for creating new entries
        FloatingActionButton createButton = findViewById(R.id.homescreen_fab);
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventHomeActivity.this, EventCreatorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
        });

        ImageButton refresh=findViewById(R.id.refreshButton);
        refresh.setOnClickListener(view -> fetchEventsFromFirebase());
        // Initialize Facility List Button and set click listener
        Button facilityListButton = findViewById(R.id.facility_list_button);
        facilityListButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventHomeActivity.this, FacilityListActivity.class);
            startActivity(intent);
        });

        // Initialize NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setupNavigationDrawer(navigationView);
    }

    private void setupNavigationDrawer(NavigationView navigationView) {
        // Logic to handle navigation view interactions (if needed)
    }

    private void fetchEventsFromFirebase() {
        Log.d("EventHomeActivity", "Starting Firebase fetch...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("EventHomeActivity", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events); // Update the adapter with the new list of events
            }

            @Override
            public void onEventCreated(String eventId) {
                Log.d("EventHomeActivity", "Event created with ID: " + eventId);
            }

            @Override
            public void onError(Exception e) {
                Log.e("EventHomeActivity", "Error fetching events", e);
            }
        });
    }

    // Handle the result from EventCreatorActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK && data != null) {
            // Retrieve data from EventOptionsActivity
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
            Uri imageUri = null;
            String imageUriString = data.getStringExtra("imageUri");
            if (imageUriString != null && !imageUriString.isEmpty()) {
                imageUri = Uri.parse(imageUriString);
            }

            // Display a message and create a new event
            Toast.makeText(this, "Event Created: " + name, Toast.LENGTH_SHORT).show();

            // Use the createEvent method from EventController
            eventController.createEvent(
                    name,
                    detail,
                    rules,
                    deadline,
                    attendees,
                    entrants,
                    startDate,
                    ticketPrice,
                    geolocationEnabled,
                    notificationsEnabled,
                    imageUri,
                    facility,
                    new EventController.EventCallback() {
                        @Override
                        public void onEventListLoaded(ArrayList<Event> events) {
                            Log.d("EventHomeActivity", "New event added. Updating event list with " + events.size() + " items.");
                            adapter.updateEventList(events);
                        }

                        @Override
                        public void onEventCreated(String eventId) {
                            // Handle the event creation success, e.g., log or show a message
                            Log.d("EventHomeActivity", "Event created successfully with ID: " + eventId);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("EventHomeActivity", "Error adding event", e);
                        }
                    }
            );
        }
    }
}
