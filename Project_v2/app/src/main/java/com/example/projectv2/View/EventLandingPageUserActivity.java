/**
 * Activity that displays detailed information about an event for users and allows them to join or leave the event.
 * Provides options to view event details, check geolocation requirements, and manage event participation.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;
import com.bumptech.glide.Glide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Utils.DBUtils;
import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.HashMap;
import java.util.Map;

/**
 * EventLandingPageUserActivity displays event details and allows the user to join or leave the event.
 * It also checks geolocation settings for the event and displays warnings if necessary.
 */
public class EventLandingPageUserActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView, geolocationWarningView;
    private Button joinEventButton, accept_button, decline_button, leaveEventButton;
    private FirebaseFirestore db;
    private int entrantsNum;
    private int entrantListSize;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private View currentView;
    private String currentEventID;
    private String currentUserID;

    String eventID, name, details, rules, deadline, startDate, price, imageUriString,userID;
    DBUtils dbUtils = new DBUtils();

    /**
     * Called when the activity is created. Initializes views with event data and configures buttons
     * for joining and leaving the event. Checks geolocation settings and sets up additional options.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_user);
        db = FirebaseFirestore.getInstance();

        topBarUtils.topBarSetup(this, "Event", View.INVISIBLE);

        // Initialize views
        eventImageView = findViewById(R.id.event_picture);
        eventNameView = findViewById(R.id.event_name_view);
        eventDetailsView = findViewById(R.id.event_description_view);
        eventRulesView = findViewById(R.id.event_rules_view);
        eventDeadlineView = findViewById(R.id.event_deadline_view);
        eventCountdownView = findViewById(R.id.event_countdown_view);
        eventPriceView = findViewById(R.id.event_price_view);
        joinEventButton = findViewById(R.id.event_join_button);
        geolocationWarningView = findViewById(R.id.geolocation_warning_view);
        leaveEventButton = findViewById(R.id.event_leave_button);
        accept_button = findViewById(R.id.event_accept_button);
        decline_button = findViewById(R.id.event_decline_button);

        // Retrieve event data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras!=null && extras.size()<2){
            eventID = intent.getStringExtra("eventID");
            fetchEventDetails(eventID);
        }else{
            eventID = intent.getStringExtra("eventID");
            name = intent.getStringExtra("name");
            details = intent.getStringExtra("details");
            rules = intent.getStringExtra("rules");
            deadline = intent.getStringExtra("deadline");
            startDate = intent.getStringExtra("startDate");
            price = intent.getStringExtra("price");
            imageUriString = intent.getStringExtra("imageUri");
            userID = intent.getStringExtra("user");}

        // Check if the deadline has passed and update the UI
        if (deadline != null) {
            checkDeadlinePassed(deadline);
        }

        checkGeolocationEnabled(eventID);

        @SuppressLint("HardwareIds")
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        isEntrant(deviceID, isEntrant -> {
            isOwner(deviceID, isOwner -> {
                if (!(isEntrant || isOwner)) {
                    Log.d("Result", "Device is not an entrant/owner");
                    joinEventButton.setVisibility(View.VISIBLE);
                }
            });
        });

        isWaiting(deviceID, isWaiting -> {
            if (isWaiting) {
                Log.d("Result", "Device is waiting");
                joinEventButton.setVisibility(View.GONE);
                leaveEventButton.setVisibility(View.VISIBLE);
            }
        });

        isSelected(deviceID, isSelected -> {
            if (isSelected) {
                Log.d("Result", "Device is selected");
                joinEventButton.setVisibility(View.GONE);
                leaveEventButton.setVisibility(View.GONE);
                accept_button.setVisibility(View.VISIBLE);
                decline_button.setVisibility(View.VISIBLE);
            }
        });

        joinEventButton.setOnClickListener(view -> {
            if (isDeadlinePassed) {
                // Show the toast message again if the deadline has passed
                Toast.makeText(this, "The deadline for this event has passed. You cannot join.", Toast.LENGTH_LONG).show();
                Log.d("JoinEvent", "Join attempt blocked due to deadline.");
            } else {
                // Proceed with the join event logic
                joinEvent(view, eventID, userID);
            }
        });

        leaveEventButton.setOnLongClickListener(view -> {
            promptLeaveEvent(view, eventID, userID);
            return true;
        });

        accept_button.setOnClickListener(v -> addAttendee(eventID, deviceID));

        decline_button.setOnClickListener(v -> addCancelled(eventID, deviceID));

        // Set data to views with fallback if values are null
        setEventData(name, details, rules, deadline, startDate, price, imageUriString);

    }

    public interface EntrantCallback {
        void onEntrantResult(boolean isEntrant);
    }

    public interface SelectedCallback {
        void onSelectedResult(boolean isSelected);
    }

    public interface WaitingCallback {
        void onWaitingResult(boolean isWaiting);
    }

    public interface OwnerCallback {
        void onOwnerResult(boolean isOwner);
    }

    /**
     * Checks if the device ID is an entrant for the event.
     *
     * @param deviceID the device ID to check
     * @param callback the callback to return the result
     */
    private void isEntrant(String deviceID, EntrantCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Check sublists in entrantList
                        Map<String, Object> entrantList = (Map<String, Object>) document.get("entrantList");
                        if (entrantList != null) {
                            for (String key : entrantList.keySet()) {
                                List<String> sublist = (List<String>) entrantList.get(key);
                                if (sublist != null && sublist.contains(deviceID)) {
                                    Log.d("Entrant", "DeviceID found in sublist: " + key);
                                    callback.onEntrantResult(true);
                                    return; // Exit after finding the deviceID
                                }
                            }
                        }
                        Log.d("Entrant", "DeviceID not found in any sublist");
                        callback.onEntrantResult(false);
                    } else {
                        Log.d("Entrant", "Document does not exist");
                        callback.onEntrantResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Entrant", "Error fetching document", e);
                    callback.onEntrantResult(false);
                });
    }

    /**
     * Checks if the device ID is selected for the event.
     *
     * @param deviceID the device ID to check
     * @param callback the callback to return the result
     */
    private void isSelected(String deviceID, SelectedCallback callback) {
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    List<String> selectedList = (List<String>) document.get("entrantList.Selected");

                    if (selectedList != null && selectedList.contains(deviceID)) {
                        Log.d("Selected", "Value exists in the list");
                        callback.onSelectedResult(true);
                    } else {
                        Log.d("Selected", "Value does not exist in the list");
                        callback.onSelectedResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Selected", "Error fetching document", e);
                    callback.onSelectedResult(false);
                });
    }

    /**
     * Checks if the device ID is waiting for the event.
     *
     * @param deviceID the device ID to check
     * @param callback the callback to return the result
     */
    private void isWaiting(String deviceID, WaitingCallback callback) {
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    List<String> waitingList = (List<String>) document.get("entrantList.Waiting");

                    if (waitingList != null && waitingList.contains(deviceID)) {
                        Log.d("Waiting", "Value exists in the list");
                        callback.onWaitingResult(true);
                    } else {
                        Log.d("Waiting", "Value does not exist in the list");
                        callback.onWaitingResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Waiting", "Error fetching document", e);
                    callback.onWaitingResult(false);
                });
    }

    /**
     * Checks if the device ID is the owner of the event.
     *
     * @param deviceID the device ID to check
     * @param callback the callback to return the result
     */
    private void isOwner(String deviceID, OwnerCallback callback) {
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    String owner = (String) document.get("owner");

                    if (Objects.equals(deviceID, owner)) {
                        Log.d("Owner", "User is an owner");
                        callback.onOwnerResult(true);
                    } else {
                        Log.d("Owner", "User is not an owner");
                        callback.onOwnerResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Owner", "Error fetching document", e);
                    callback.onOwnerResult(false);
                });
    }

    /**
     * Checks if the user ID is an attendee of the event.
     *
     * @param userID the device ID to check
     * @param eventID the event ID to check in
     */
    private void addAttendee(String eventID, String userID) {
        Log.d("Selected", "addAttendee: " + userID);

        db.collection("events").document(eventID)
                .update("entrantList.Attendee", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(aVoid -> Log.d("Selected", "User added to Attendee list"))
                .addOnFailureListener(e -> Log.d("Selected", "Error adding user", e));

        db.collection("events").document(eventID)
                .update("entrantList.Selected", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> Log.d("Selected", "User removed from Selected list"))
                .addOnFailureListener(e -> Log.d("Selected", "Error removing user", e));

        NotificationService notificationService = new NotificationService();
        String eventName = getIntent().getStringExtra("name");
        Notification notification = new Notification(userID, "Welcome to " + eventName, true, false);
        notificationService.sendNotification(this, notification, eventID);

        accept_button.setVisibility(View.INVISIBLE);
        decline_button.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds the user to the Cancelled list and selects a new user from the Waiting list.
     *
     * @param eventID the event ID
     * @param userID  the user ID
     */
    private void addCancelled(String eventID, String userID) {
        Log.d("Selected", "addCancelled: " + userID);

        // Add the rejecting user to the Cancelled list
        db.collection("events").document(eventID)
                .update("entrantList.Cancelled", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(aVoid -> Log.d("Selected", "User added to Cancelled list"))
                .addOnFailureListener(e -> Log.d("Selected", "Error adding user to Cancelled list", e));

        // Remove the rejecting user from the Selected list
        db.collection("events").document(eventID)
                .update("entrantList.Selected", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Selected", "User removed from Selected list");

                    // Fetch the current waiting list
                    db.collection("events").document(eventID).get()
                            .addOnSuccessListener(document -> {
                                List<String> waitingList = (List<String>) document.get("entrantList.Waiting");

                                if (waitingList != null && !waitingList.isEmpty()) {
                                    // Select a random user from the waiting list
                                    String newSelectedUser = waitingList.get((int) (Math.random() * waitingList.size()));

                                    // Add the new user to the Selected list
                                    db.collection("events").document(eventID)
                                            .update("entrantList.Selected", FieldValue.arrayUnion(newSelectedUser))
                                            .addOnSuccessListener(innerVoid -> {
                                                Log.d("Selected", "New user added to Selected list: " + newSelectedUser);

                                                // Remove the new user from the Waiting list
                                                db.collection("events").document(eventID)
                                                        .update("entrantList.Waiting", FieldValue.arrayRemove(newSelectedUser))
                                                        .addOnSuccessListener(innerInnerVoid -> Log.d("Selected", "New user removed from Waiting list: " + newSelectedUser))
                                                        .addOnFailureListener(e -> Log.d("Selected", "Error removing new user from Waiting list", e));

                                                NotificationService notificationService = new NotificationService();
                                                String eventName = getIntent().getStringExtra("name");
                                                Notification notification = new Notification(newSelectedUser, "Congratulations! You have been chosen to attend " + eventName, true, false);
                                                notificationService.sendNotification(this, notification, eventID);
                                            })
                                            .addOnFailureListener(e -> Log.d("Selected", "Error adding new user to Selected list", e));
                                } else {
                                    Log.d("Selected", "No users left in the Waiting list to move to Selected list");
                                }
                            })
                            .addOnFailureListener(e -> Log.d("Selected", "Error fetching Waiting list", e));
                })
                .addOnFailureListener(e -> Log.d("Selected", "Error removing user from Selected list", e));

        NotificationService notificationService = new NotificationService();
        String eventName = getIntent().getStringExtra("name");
        Notification notification = new Notification(userID, "Your cancellation of " + eventName + " is confirmed.", true, false);
        notificationService.sendNotification(this, notification, eventID);

        // Hide the accept and decline buttons
        accept_button.setVisibility(View.INVISIBLE);
        decline_button.setVisibility(View.INVISIBLE);
    }

    private boolean isDeadlinePassed = false;

    /**
     * Checks if the event deadline has passed and displays a toast message if it has.
     *
     * @param deadline the deadline of the event in "dd-MM-yyyy" format
     */
    private void checkDeadlinePassed(String deadline) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date deadlineDate = dateFormat.parse(deadline);
            Date currentDate = dateFormat.parse(dateFormat.format(new Date()));

            if (currentDate.equals(deadlineDate) || currentDate.before(deadlineDate)) {
                isDeadlinePassed = false;
            } else {
                isDeadlinePassed = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("DeadlineCheck", "Error parsing deadline date: " + e.getMessage());
        }
    }

    /**
     * Fetches the details of an event.
     *
     * @param eventid unique id of the event
     */
    private void fetchEventDetails(String eventid) {
        dbUtils.fetchEvent(eventid, eventDetails -> {
            if (eventDetails != null) {

                name = eventDetails.get("name");
                details = eventDetails.get("details");
                rules = eventDetails.get("rules");
                deadline = eventDetails.get("deadline");
                startDate = eventDetails.get("startDate");
                price = eventDetails.get("price");
                imageUriString = eventDetails.get("imageUri");
                userID = eventDetails.get("user");

                // Update UI with the fetched data
                runOnUiThread(() -> {
                    setEventData(name, details, rules, deadline, startDate, price, imageUriString);

                    // Check if the deadline has passed and update the UI
                    if (deadline != null) {
                        checkDeadlinePassed(deadline);
                    }

                    // Configure the join event button after data is loaded
                    checkGeolocationEnabled(eventID);

                    isEntrant(userID, isEntrant -> {
                        if (!isEntrant) {
                            Log.d("Result", "Device is not an entrant");
                            joinEventButton.setVisibility(View.VISIBLE);
                        }
                    });

                    isWaiting(userID, isWaiting -> {
                        if (isWaiting) {
                            Log.d("Result", "Device is waiting");
                            joinEventButton.setVisibility(View.GONE);
                            leaveEventButton.setVisibility(View.VISIBLE);
                        }
                    });

                    isSelected(userID, isSelected -> {
                        if (isSelected) {
                            Log.d("Result", "Device is selected");
                            joinEventButton.setVisibility(View.GONE);
                            leaveEventButton.setVisibility(View.GONE);
                            accept_button.setVisibility(View.VISIBLE);
                            decline_button.setVisibility(View.VISIBLE);
                        }
                    });

                    joinEventButton.setOnClickListener(view -> joinEvent(view, eventID, userID));

                    leaveEventButton.setOnLongClickListener(view -> {
                        promptLeaveEvent(view, eventID, userID);
                        return true;
                    });

                    accept_button.setOnClickListener(v -> addAttendee(eventID, userID));

                    decline_button.setOnClickListener(v -> addCancelled(eventID, userID));
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(EventLandingPageUserActivity.this,
                            "Failed to load event details", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    /**
     * Checks if geolocation is enabled for the event and displays a warning if it is required.
     *
     * @param eventId the ID of the event to check
     */
    private void checkGeolocationEnabled(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocationEnabled");
                if (geolocationEnabled != null && geolocationEnabled) {
                    geolocationWarningView.setVisibility(View.VISIBLE);
                } else {
                    geolocationWarningView.setVisibility(View.GONE);
                }
            } else {
                Log.d("EventLandingPageUser", "No such event found");
            }
        }).addOnFailureListener(e -> Log.e("EventLandingPageUser", "Error checking geolocation: ", e));
    }

    private void prepareJoinEvent(View view, String eventID, String userID) {
        currentView = view;
        currentEventID = eventID;
        currentUserID = userID;
        joinEvent(view, eventID, userID);
    }

    /**
     * Joins the event by adding the user ID to the entrant and waiting lists in Firestore.
     *
     * @param view   the view triggering the action
     * @param eventID the event ID
     * @param userID  the user ID
     */
    private void joinEvent(View view, String eventID, String userID) {
        // Check location permissions first
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                DocumentReference eventRef = db.collection("events").document(eventID);
                DocumentReference userRef = db.collection("Users").document(userID);

                eventRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String entrantsString = document.getString("entrants");
                            entrantsNum = entrantsString != null && !entrantsString.isEmpty()
                                    ? Integer.parseInt(entrantsString)
                                    : Integer.MAX_VALUE;

                            List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                            entrantListSize = (waitingList != null) ? waitingList.size() : 0;

                            if (entrantListSize <= (entrantsNum - 1)) {
                                // Update user location if location is available
                                if (location != null) {
                                    Map<String, Object> locationUpdate = new HashMap<>();
                                    locationUpdate.put("latitude", location.getLatitude());
                                    locationUpdate.put("longitude", location.getLongitude());

                                    userRef.update(locationUpdate)
                                            .addOnSuccessListener(aVoid -> {
                                                // After updating location, add user to event waiting list
                                                eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                                                        .addOnSuccessListener(v -> showJoinSuccess(view))
                                                        .addOnFailureListener(e -> showJoinFailure(view, e));
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Location", "Failed to update user location", e);
                                                // Still proceed with adding to event if location update fails
                                                eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                                                        .addOnSuccessListener(v -> showJoinSuccess(view))
                                                        .addOnFailureListener(failE -> showJoinFailure(view, failE));
                                            });
                                } else {
                                    // No location available, just add to waiting list
                                    eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                                            .addOnSuccessListener(v -> showJoinSuccess(view))
                                            .addOnFailureListener(e -> showJoinFailure(view, e));
                                }
                            } else {
                                Snackbar.make(view, "Waiting list is full. Try again later.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                Log.e("Location", "Error getting location", e);
                // Fallback to adding to event without location
                DocumentReference eventRef = db.collection("events").document(eventID);
                eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                        .addOnSuccessListener(v -> showJoinSuccess(view))
                        .addOnFailureListener(failE -> showJoinFailure(view, failE));
            });
        } else {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        joinEventButton.setVisibility(View.GONE);
        leaveEventButton.setVisibility(View.VISIBLE);
    }

    // Add this method to handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permissions were just granted, retry joining the event
                joinEvent(currentView, currentEventID, currentUserID);
            } else {
                Toast.makeText(this, "Location permission is required to join the event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Prompts the user to confirm if they want to leave the event.
     *
     * @param view    the view triggering the action
     * @param eventID the event ID
     * @param userID  the user ID
     */
    private void promptLeaveEvent(View view, String eventID, String userID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Event");
        builder.setMessage("Are you sure you want to leave this event?");
        builder.setPositiveButton("Yes", (dialog, which) -> leaveEvent(view, eventID, userID));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Leaves the event by removing the user ID from the entrant and waiting lists in Firestore.
     *
     * @param view    the view triggering the action
     * @param eventID the event ID
     * @param userID  the user ID
     */
    private void leaveEvent(View view, String eventID, String userID) {
        DocumentReference eventRef = db.collection("events").document(eventID);

        eventRef.update("entrantList.Waiting", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> showLeaveSuccess(view))
                .addOnFailureListener(e -> showLeaveFailure(view, e));

        joinEventButton.setVisibility(View.VISIBLE);
        leaveEventButton.setVisibility(View.GONE);
    }

    /**
     * Sets event data in views with fallback values if data is missing.
     *
     * @param name          the event name
     * @param details       the event details
     * @param rules         the event rules
     * @param deadline      the event deadline
     * @param startDate     the event start date
     * @param price         the event price
     * @param imageUriString the event image URI as a string
     */
    private void setEventData(String name, String details, String rules, String deadline, String startDate, String price, String imageUriString) {
        eventNameView.setText(name != null ? name : "No name");
        eventDetailsView.setText(details != null ? details : "No details");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");

        try {
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriString);
                eventImageView.setImageURI(imageUri);
            } else {
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            eventImageView.setImageResource(R.drawable.placeholder_event);
        }
        loadEventImage(name);
    }
    /**
     * Loads the event image using the ImageController.
     *
     * @param eventName The name of the event.
     */
    private void loadEventImage(String eventName) {
        ImageController imageController = new ImageController();
        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                // Use Glide to load the image into the ImageView
                Glide.with(EventLandingPageUserActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                        .error(R.drawable.placeholder_event) // Placeholder if loading fails
                        .centerCrop()
                        .into(eventImageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                // Log the error and show the placeholder image
                Log.e("EventLandingPageUser", "Failed to load image for event: " + eventName, e);
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }
    /**
     * Shows a success message when the user successfully joins the event.
     *
     * @param view the view triggering the action
     */
    private void showJoinSuccess(View view) {
        Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
//        joinEventButton.setText("Leave");
//        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.leaveevent_icon, 0, 0, 0);
//        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiEmphasis)));
    }

    /**
     * Shows a failure message when there is an error joining the event.
     *
     * @param view the view triggering the action
     * @param e    the exception detailing the error
     */
    private void showJoinFailure(View view, Exception e) {
        Snackbar.make(view, "Failed to join event: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        e.printStackTrace();
    }

    /**
     * Shows a success message when the user successfully leaves the event.
     *
     * @param view the view triggering the action
     */
    private void showLeaveSuccess(View view) {
        Snackbar.make(view, "Successfully left the event", Snackbar.LENGTH_LONG).show();
//        joinEventButton.setText("Join");
//        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.joinevent_icon, 0, 0, 0);
//        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiAccent)));
    }

    /**
     * Shows a failure message when there is an error leaving the event.
     *
     * @param view the view triggering the action
     * @param e    the exception detailing the error
     */
    private void showLeaveFailure(View view, Exception e) {
        Snackbar.make(view, "Failed to leave event: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        e.printStackTrace();
    }
}