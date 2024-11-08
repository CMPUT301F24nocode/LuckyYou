/**
 * Activity for displaying and managing entrant lists for an event. Provides filtering options
 * to view different entrant lists (e.g., waiting list, selected list, attendee list),
 * and allows sending notifications to entrants.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
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

/**
 * EntrantListActivity displays and manages different entrant lists for an event,
 * such as waiting list, selected list, and cancelled list. Users can filter the list
 * and send notifications to specific groups of entrants.
 */
public class EntrantListActivity extends AppCompatActivity {
    private static final String TAG = "EntrantListActivity";

    private RecyclerView entrantRecyclerView;
    private EntrantListAdapter adapter;
    private FirebaseFirestore db;
    private Spinner filterSpinner;
    private Button sendNotifAllView;
    private List<String> waitingList = new ArrayList<>();

    /**
     * Called when the activity is created. Sets up the entrant list RecyclerView,
     * filter options, and loads the full list of entrants by default.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_list);

        topBarUtils.topBarSetup(this, "Entrant List", View.INVISIBLE);

        // Initialize RecyclerView and set its layout to a vertical list
        entrantRecyclerView = findViewById(R.id.entrantRecyclerView);
        entrantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore database instance and the adapter with an empty list
        db = FirebaseFirestore.getInstance();
        adapter = new EntrantListAdapter(this, new ArrayList<>());
        entrantRecyclerView.setAdapter(adapter);

        filterSpinner = findViewById(R.id.entrant_list_dropdown);
        sendNotifAllView = findViewById(R.id.send_notification_toAll_button);
        setupFilterSpinner();

        loadEntrantList();
    }

    /**
     * Sets up the filter spinner with options to display different entrant lists
     * (e.g., Entrant List, Waiting List, Selected List, Cancelled List, Attendee List).
     * Listens for changes in selection and loads the corresponding entrant list.
     */
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

    /**
     * Loads the full entrant list from Firestore for the event and updates the RecyclerView adapter.
     */
    private void loadEntrantList() {
        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> entrantList = (List<String>) documentSnapshot.get("entrantList.EntrantList");
                        if (entrantList != null) {
                            adapter.updateEntrantList(entrantList);
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

    /**
     * Loads the attendee list from Firestore for the event and updates the RecyclerView adapter.
     * Sends a notification to all attendees in the list.
     *
     * @param sendNotifAll the button to trigger sending notifications to all attendees
     */
    private void loadAttendeeList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> attendees = (List<String>) documentSnapshot.get("entrantList.Attendee");
                        if (attendees != null) {
                            adapter.updateEntrantList(attendees);
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

    /**
     * Loads the waiting list from Firestore for the event and updates the RecyclerView adapter.
     * Sends a notification to all users in the waiting list.
     *
     * @param sendNotifAll the button to trigger sending notifications to all waiting list users
     */
    private void loadWaitingList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");

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

    /**
     * Loads the cancelled list from Firestore for the event and updates the RecyclerView adapter.
     * Sends a notification to all users in the cancelled list.
     *
     * @param sendNotifAll the button to trigger sending notifications to all cancelled users
     */
    private void loadCancelled(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> cancelledList = (List<String>) documentSnapshot.get("entrantList.Cancelled");
                        if (cancelledList != null) {
                            adapter.updateEntrantList(cancelledList);
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
                Notification notification = new Notification(userId, "Your cancellation of " + eventName + " is confirmed.", true, false);
                notificationService.sendNotification(notification);
            }
        });
    }

    /**
     * Loads the selected list from Firestore for the event and updates the RecyclerView adapter.
     * Sends a notification to all selected users.
     *
     * @param sendNotifAll the button to trigger sending notifications to all selected users
     */
    private void loadSelectedList(Button sendNotifAll) {
        String eventId = getIntent().getStringExtra("eventId");

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> selectedList = (List<String>) documentSnapshot.get("entrantList.Selected");
                        if (selectedList != null) {
                            adapter.updateEntrantList(selectedList);
                        } else {
                            Toast.makeText(this, "No selected entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading selected list", e);
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
