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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Utils.DBUtils;
import com.example.projectv2.Controller.EntrantListAdapter;
import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentSnapshot;
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
    public FirebaseFirestore db;
    private Spinner filterSpinner;
    private Button sendNotifAllView, removeAllEntrants, sendNotifView;
    private List<String> documentIds = new ArrayList<>();
    private List<String> waitingList = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();
    private List<String> cancelledList = new ArrayList<>();
    private List<String> attendees = new ArrayList<>();


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
        adapter = new EntrantListAdapter(this, new ArrayList<>(), new ArrayList<>());
        entrantRecyclerView.setAdapter(adapter);

        filterSpinner = findViewById(R.id.entrant_list_dropdown);
        setupFilterSpinner();

        sendNotifAllView = findViewById(R.id.send_notification_toAll_button);
        sendNotifAllView.setOnClickListener(v -> showPopup(documentIds));

        removeAllEntrants = findViewById(R.id.cancel_all_entrants_button);
        String eventId = getIntent().getStringExtra("eventId");
        removeAllEntrants.setOnClickListener(v -> DBUtils.removeUsers(documentIds, eventId));

        // Add a touch listener to the root layout to hide buttons on outside clicks
        View rootView = findViewById(android.R.id.content); // Get the root view
        rootView.setOnTouchListener((v, event) -> {
            // Detect if the touch is outside the RecyclerView
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Get the coordinates of the touch
                int[] location = new int[2];
                entrantRecyclerView.getLocationOnScreen(location);
                float x = event.getRawX();
                float y = event.getRawY();

                // Check if the touch is outside RecyclerView bounds
                if (x < location[0] || x > location[0] + entrantRecyclerView.getWidth()
                        || y < location[1] || y > location[1] + entrantRecyclerView.getHeight()) {
                    adapter.clearSelection();
                    v.performClick(); // Perform a click to satisfy accessibility requirements
                }
            }
            return false; // Allow other touch events to propagate
        });
    }

    /**
     * Shows a popup dialog to send a notification to all users in the entrant list.
     *
     * @param documentIds the list of document IDs to send notifications to
     */
    private void showPopup(List<String> documentIds) {
        String eventId = getIntent().getStringExtra("eventId");
        SendNotificationOverlay overlay = SendNotificationOverlay.newInstance(this, new ArrayList<>(documentIds), eventId);
        overlay.show(getSupportFragmentManager(), "SendNotificationOverlay");
    }

    /**
     * Sets up the filter spinner with options to display different entrant lists
     * (e.g., Entrant List, Waiting List, Selected List, Cancelled List, Attendee List).
     * Listens for changes in selection and loads the corresponding entrant list.
     */
    private void setupFilterSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Waiting List", "Selected List", "Cancelled List", "Attendee List"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when an item in the spinner is selected.
             *
             * @param parent   the AdapterView where the selection happened
             * @param view     the view within the AdapterView that was selected
             * @param position the position of the view in the adapter
             * @param id       the row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                switch (selectedOption) {
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

            /**
             * Called when the selection disappears from this view.
             *
             * @param parent the AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadWaitingList(sendNotifAllView);
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
                        documentIds = (List<String>) documentSnapshot.get("entrantList.Waiting");
                        Log.d("FetchNames", "loadWaitingListIDs: " + documentIds);

                        db = FirebaseFirestore.getInstance();
                        waitingList.clear();

                        // Counter to track completed Firestore tasks
                        final int[] completedTasks = {0};

                        for (String documentId : documentIds) {
                            db.collection("Users").document(documentId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.e("FetchNames", "Error fetching document for ID: " + documentId, task.getException());
                                            return;
                                        }

                                        DocumentSnapshot document = task.getResult();
                                        String name = document != null ? document.getString("name") : null;
                                        if (name != null) {
                                            waitingList.add(name);
                                            Log.d("FetchNames", "Name: " + name);
                                        } else {
                                            Log.d("FetchNames", "Document not found or name is null for ID: " + documentId);
                                        }

                                        // Increment completed tasks
                                        completedTasks[0]++;

                                        // When all tasks are done, update the adapter
                                        if (completedTasks[0] == documentIds.size()) {
                                            Log.d("FetchNames", "All names fetched: " + waitingList);
                                            if (!waitingList.isEmpty()) {
                                                adapter.updateEntrantList(waitingList, documentIds);
                                            } else {
                                                Toast.makeText(this, "No entrants in Waiting List.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        if (waitingList != null) {
                            adapter.updateEntrantList(waitingList, documentIds);
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading Waiting List", e));
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
                        documentIds = (List<String>) documentSnapshot.get("entrantList.Selected");
                        waitingList = (List<String>) documentSnapshot.get("entrantList.Waiting");

                        db = FirebaseFirestore.getInstance();
                        selectedList.clear();

                        // Counter to track completed Firestore tasks
                        final int[] completedTasks = {0};

                        for (String documentId : documentIds) {
                            db.collection("Users").document(documentId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.e("FetchNames", "Error fetching document for ID: " + documentId, task.getException());
                                            return;
                                        }

                                        DocumentSnapshot document = task.getResult();
                                        String name = document != null ? document.getString("name") : null;
                                        if (name != null) {
                                            selectedList.add(name);
                                            Log.d("FetchNames", "Name: " + name);
                                        } else {
                                            Log.d("FetchNames", "Document not found or name is null for ID: " + documentId);
                                        }

                                        // Increment completed tasks
                                        completedTasks[0]++;

                                        // When all tasks are done, update the adapter
                                        if (completedTasks[0] == documentIds.size()) {
                                            Log.d("FetchNames", "All names fetched: " + selectedList);
                                            if (!selectedList.isEmpty()) {
                                                adapter.updateEntrantList(selectedList, documentIds);
                                                sendNotificationsToSelectedAndWaitingLists(eventId);
                                            } else {
                                                Toast.makeText(this, "No entrants in Waiting List.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        if (selectedList != null) {
                            adapter.updateEntrantList(selectedList, documentIds);
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading selected list", e);
                    Toast.makeText(this, "Failed to load selected list", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sends notifications to all users in the selected list and remaining users in the waiting list.
     *
     * @param eventId the ID of the event to send notifications for
     */
    private void sendNotificationsToSelectedAndWaitingLists(String eventId) {
        if (documentIds == null || documentIds.isEmpty()) {
            Log.d(TAG, "No users in the selected list to notify.");
            return;
        }

        NotificationService notificationService = new NotificationService();
        String eventName = getIntent().getStringExtra("name");

        // Notify users in the selected list
        for (String userId : documentIds) {
            Notification notification = new Notification(userId, "Congratulations! You have been chosen to attend " + eventName, true, false);
            notificationService.sendNotification(this, notification, eventId);
        }

        // Notify remaining users in the waiting list
        if (waitingList != null) {
            List<String> remainingWaitingList = new ArrayList<>(waitingList);
            remainingWaitingList.removeAll(documentIds);

            for (String userId : remainingWaitingList) {
                Notification notification = new Notification(userId, "You were not selected for " + eventName + ". Don't worry, you may get another chance. Stay tuned!", true, false);
                notificationService.sendNotification(this, notification, eventId);
            }
        }

        Log.d(TAG, "Notifications sent to selected and remaining waiting list users.");
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
                        documentIds = (List<String>) documentSnapshot.get("entrantList.Cancelled");

                        db = FirebaseFirestore.getInstance();
                        cancelledList.clear();

                        // Counter to track completed Firestore tasks
                        final int[] completedTasks = {0};

                        for (String documentId : documentIds) {
                            db.collection("Users").document(documentId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.e("FetchNames", "Error fetching document for ID: " + documentId, task.getException());
                                            return;
                                        }

                                        DocumentSnapshot document = task.getResult();
                                        String name = document != null ? document.getString("name") : null;
                                        if (name != null) {
                                            cancelledList.add(name);
                                            Log.d("FetchNames", "Name: " + name);
                                        } else {
                                            Log.d("FetchNames", "Document not found or name is null for ID: " + documentId);
                                        }

                                        // Increment completed tasks
                                        completedTasks[0]++;

                                        // When all tasks are done, update the adapter
                                        if (completedTasks[0] == documentIds.size()) {
                                            Log.d("FetchNames", "All names fetched: " + cancelledList);
                                            if (!cancelledList.isEmpty()) {
                                                adapter.updateEntrantList(cancelledList, documentIds);
                                            } else {
                                                Toast.makeText(this, "No entrants in Waiting List.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                        if (cancelledList != null) {
                            adapter.updateEntrantList(cancelledList, documentIds);
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading cancelled list", e);
                    Toast.makeText(this, "Failed to load cancelled list", Toast.LENGTH_SHORT).show();
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
                        documentIds = (List<String>) documentSnapshot.get("entrantList.Attendee");

                        db = FirebaseFirestore.getInstance();
                        attendees.clear();

                        // Counter to track completed Firestore tasks
                        final int[] completedTasks = {0};

                        for (String documentId : documentIds) {
                            db.collection("Users").document(documentId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.e("FetchNames", "Error fetching document for ID: " + documentId, task.getException());
                                            return;
                                        }

                                        DocumentSnapshot document = task.getResult();
                                        String name = document != null ? document.getString("name") : null;
                                        if (name != null) {
                                            attendees.add(name);
                                            Log.d("FetchNames", "Name: " + name);
                                        } else {
                                            Log.d("FetchNames", "Document not found or name is null for ID: " + documentId);
                                        }

                                        // Increment completed tasks
                                        completedTasks[0]++;

                                        // When all tasks are done, update the adapter
                                        if (completedTasks[0] == documentIds.size()) {
                                            Log.d("FetchNames", "All names fetched: " + attendees);
                                            if (!attendees.isEmpty()) {
                                                adapter.updateEntrantList(attendees, documentIds);
                                            } else {
                                                Toast.makeText(this, "No entrants in Waiting List.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                        if (attendees != null) {
                            adapter.updateEntrantList(attendees, documentIds);
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading attendees", e));
    }
}
