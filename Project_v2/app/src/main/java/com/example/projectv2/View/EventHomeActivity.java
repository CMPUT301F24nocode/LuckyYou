package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

        // Initialize NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setupNavigationDrawer(navigationView);
    }

    private void setupNavigationDrawer(NavigationView navigationView) {
        // Logic to handle navigation view interactions (if needed)
    }

    // Fetch events from Firebase Firestore
    private void fetchEventsFromFirebase() {
        Log.d("EventHomeActivity", "Starting Firebase fetch...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("EventHomeActivity", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events);
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
            Event newEvent = new Event(name, detail, rules, deadline, startDate, ticketPrice, imageUri);

            // Add the new event to Firestore using EventController
            eventController.addEventToFirestore(newEvent, new EventController.EventCallback() {
                @Override
                public void onEventListLoaded(ArrayList<Event> events) {
                    Log.d("EventHomeActivity", "New event added. Updating event list with " + events.size() + " items.");
                    adapter.updateEventList(events);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("EventHomeActivity", "Error adding event", e);
                }
            });
        }
    }
}
