// MainActivity.java
package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
=======

>>>>>>> f25918a6076f87611ebd5d39857307737358af62
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Controller.EventDisplayController;
import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.Event;
<<<<<<< HEAD
import com.example.projectv2.MainActivity;
=======

>>>>>>> f25918a6076f87611ebd5d39857307737358af62
import com.example.projectv2.R;

import java.util.ArrayList;

public class EventHomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private EventController eventController;
    private EventDisplayController eventDisplayController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_page);

        // Initialize ListView and EventDisplayController
        ListView listView = findViewById(R.id.list_view);
        eventDisplayController = new EventDisplayController(this, listView, new ArrayList<>());

        // Initialize EventController for Firestore operations
        eventController = new EventController(this);

        // Fetch events from Firestore and update display
        fetchEventsFromFirebase();

        // Set up the create button to open EventCreatorActivity
        Button createButton = findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> {
<<<<<<< HEAD
            Intent intent = new Intent(EventHomeActivity.this, EventCreatorActivity.class);
=======

            Intent intent = new Intent(EventHomeActivity.this, EventCreatorActivity.class);

>>>>>>> f25918a6076f87611ebd5d39857307737358af62
            startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
        });

        // Set up click listener for the list items
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventDisplayController.getEventAt(position);
            // Prepare intent and pass event data to EventLandingPageActivity
<<<<<<< HEAD
            Intent intent = new Intent(EventHomeActivity.this, EventLandingPageActivity.class);
=======

            Intent intent = new Intent(EventHomeActivity.this, EventLandingPageActivity.class);

>>>>>>> f25918a6076f87611ebd5d39857307737358af62
            intent.putExtra("name", selectedEvent.getName());
            intent.putExtra("details", selectedEvent.getDetail());
            intent.putExtra("rules", selectedEvent.getRules());
            intent.putExtra("deadline", selectedEvent.getDeadline());
            intent.putExtra("startDate", selectedEvent.getStartDate());
            intent.putExtra("price", selectedEvent.getTicketPrice());
            if (selectedEvent.getImageUri() != null) {
                intent.putExtra("imageUri", selectedEvent.getImageUri().toString());
            }
            startActivity(intent);
        });
    }

    // Fetch events from Firebase Firestore
    private void fetchEventsFromFirebase() {
        Log.d("MainActivity", "Starting Firebase fetch...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("MainActivity", "Fetched " + events.size() + " events from Firebase.");
                eventDisplayController.updateEventList(events); // Update the list display
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Error fetching events", e);
            }
        });
    }

    // Handle the result from EventCreatorActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK && data != null) {
            // Retrieve event data
            String name = data.getStringExtra("name");
            String detail = data.getStringExtra("detail");
            String rules = data.getStringExtra("rules");
            String deadline = data.getStringExtra("deadline");
            String startDate = data.getStringExtra("startDate");
            String ticketPrice = data.getStringExtra("ticketPrice");

            Uri imageUri = null;
            String imageUriString = data.getStringExtra("imageUri");
            if (imageUriString != null && !imageUriString.isEmpty()) {
                imageUri = Uri.parse(imageUriString);
            }

            Event newEvent = new Event(name, detail, rules, deadline, startDate, ticketPrice, imageUri);

            // Add the new event to Firestore using EventController
            eventController.addEventToFirestore(newEvent, new EventController.EventCallback() {
                @Override
                public void onEventListLoaded(ArrayList<Event> events) {
                    Log.d("MainActivity", "New event added. Updating event list with " + events.size() + " items.");
                    eventDisplayController.updateEventList(events);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("MainActivity", "Error adding event", e);
                }
            });
        }
    }
}