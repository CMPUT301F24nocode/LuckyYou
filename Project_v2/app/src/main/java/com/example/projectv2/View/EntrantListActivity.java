package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EntrantListActivity extends AppCompatActivity {
    private static final String TAG = "EntrantListActivity";

    // UI elements for displaying the entrant list and filter options
    private RecyclerView entrantRecyclerView;
    private EntrantListAdapter adapter;
    private FirebaseFirestore db; // Firebase Firestore instance for database access
    private Spinner filterSpinner; // Spinner for filter selection
    private Button sendNotifAllView;

    private List<String> waitingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_list);

        // Set up the top bar with title and back button
        topBarUtils.topBarSetup(this, "Entrant List", View.INVISIBLE);

        // Initialize RecyclerView and set its layout to a vertical list
        entrantRecyclerView = findViewById(R.id.entrantRecyclerView);
        entrantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore database instance and the adapter with an empty list
        db = FirebaseFirestore.getInstance();
        adapter = new EntrantListAdapter(this, new ArrayList<>());
        entrantRecyclerView.setAdapter(adapter); // Set adapter to RecyclerView

        // Initialize the filter dropdown (Spinner)
        filterSpinner = findViewById(R.id.entrant_list_dropdown);
        sendNotifAllView = findViewById(R.id.send_notification_toAll_button);
        setupFilterSpinner();

        // Load the full list of entrants by default
        loadEntrantList();
    }

    // Set up the filter spinner with options: "Entrant List", "Attendees", "Declined"
    private void setupFilterSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Entrant List", "Waiting List", "Selected List", "Cancelled List", "Attendee List"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                switch (selectedOption) {
                    case "Entrant List":
                        loadEntrantList();
                        break;
                    case "Waiting List":
                        loadWaitingList(sendNotifAllView);
                        break;
                    case "Selected List":
                        loadSelectedList(sendNotifAllView);
                        break;
                    case "Cancelled List":
                        loadCancelled(sendNotifAllView);
                        break;
                    case "Attendee List":
                        loadAttendeeList(sendNotifAllView);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadEntrantList();
            }
        });
    }


    // Method to load the full entrant list from Firebase Firestore
    private void loadEntrantList() {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the entrant list from Firestore based on event ID
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the entire list of entrants from "entrantList.EntrantList" field
                        List<String> entrantList = (List<String>) documentSnapshot.get("entrantList.EntrantList");
                        if (entrantList != null) {
                            adapter.updateEntrantList(entrantList); // Update adapter with entrants
                        } else {
                            Toast.makeText(this, "No entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading entrant list", e);
                    Toast.makeText(this, "Failed to load entrant list", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to load a random selection of up to 20 attendees (FOR NOW)
    private void loadAttendeeList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the attendees list from Firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> attendees = (List<String>) documentSnapshot.get("entrantList.Attendee");
                        if (attendees != null) {
                            // Shuffle and select up to 20 random attendees
                            adapter.updateEntrantList(attendees); // Update adapter with selected attendees
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading attendees", e));

        sendNotifAll.setOnClickListener(view -> {
            NotificationService notificationService = new NotificationService();
            String eventName = getIntent().getStringExtra("name");

            for (String userId : waitingList) {
                Notification notification = new Notification(userId, "Welcome to " + eventName, true, false);
                notificationService.sendNotification(notification);
            }
        });
    }

    // Method to load the waiting list from Firestore
    private void loadWaitingList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");
//        List<String> waitingList;
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        waitingList = (List<String>) documentSnapshot.get("entrantList.Waiting");
                        if (waitingList != null) {
                            adapter.updateEntrantList(waitingList);
                        } else {
                            Toast.makeText(this, "No entrants in Waiting List.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading Waiting List", e));


        sendNotifAll.setOnClickListener(view -> {
            NotificationService notificationService = new NotificationService();
            String eventName = getIntent().getStringExtra("name");

            for (String userId : waitingList) {
                Notification notification = new Notification(userId, "You're in the waiting list for " + eventName, true, false);
                notificationService.sendNotification(notification);
            }
        });
    }

    // Method to load the cancelled list from Firestore
    private void loadCancelled(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the cancelled list from Firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> cancelledList = (List<String>) documentSnapshot.get("entrantList.Cancelled");
                        if (cancelledList != null) {
                            adapter.updateEntrantList(cancelledList); // Update adapter with cancelled entrants
                        } else {
                            Toast.makeText(this, "No cancelled entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading cancelled list", e);
                    Toast.makeText(this, "Failed to load cancelled list", Toast.LENGTH_SHORT).show();
                });

        sendNotifAll.setOnClickListener(view -> {
            NotificationService notificationService = new NotificationService();
            String eventName = getIntent().getStringExtra("name");

            for (String userId : waitingList) {
                Notification notification = new Notification(userId, "You're cancellation of " + eventName + " is confirmed.", true, false);
                notificationService.sendNotification(notification);
            }
        });
    }

    // Method to randomly select 20 users for the Selected List
    private void loadSelectedList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> selectedList = (List<String>) documentSnapshot.get("entrantList.Selected");
                        if (selectedList != null) {
                            adapter.updateEntrantList(selectedList); // Update adapter with cancelled entrants
                        } else {
                            Toast.makeText(this, "No selected entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading cancelled list", e);
                    Toast.makeText(this, "Failed to load selected list", Toast.LENGTH_SHORT).show();
                });

        sendNotifAll.setOnClickListener(view -> {
            NotificationService notificationService = new NotificationService();
            String eventName = getIntent().getStringExtra("name");

            for (String userId : waitingList) {
                Notification notification = new Notification(userId, "You have been chosen to attend " + eventName, true, false);
                notificationService.sendNotification(notification);
            }
        });
    }
}