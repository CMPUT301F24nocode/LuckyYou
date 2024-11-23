/**
 * Activity that displays detailed information about an event for users and allows them to join or leave the event.
 * Provides options to view event details, check geolocation requirements, and manage event participation.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.DBUtils;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * EventLandingPageUserActivity displays event details and allows the user to join or leave the event.
 * It also checks geolocation settings for the event and displays warnings if necessary.
 */
public class EventLandingPageUserActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView, geolocationWarningView;
    private Button joinEventButton;
    private FirebaseFirestore db;
    private int entrantsNum;
    private int entrantListSize;

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

        topBarUtils.topBarSetup(this, "Event", View.VISIBLE);

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

        // Retrieve event data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        Log.d("EventLandingPageUser", "Extras: " + extras + String.valueOf(extras.size()));

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


        // Configure the join event button
        joinEventButton.setOnClickListener(view -> joinEvent(view, eventID, userID));

        checkGeolocationEnabled(eventID);

        // Configure the long-click listener to leave the event
        joinEventButton.setOnLongClickListener(view -> {
            promptLeaveEvent(view, eventID, userID);
            return true;
        });

        // Set data to views with fallback if values are null
        setEventData(name, details, rules, deadline, startDate, price, imageUriString);

        // Set up the additional settings popup
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup());
    }

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

                    // Configure the join event button after data is loaded
                    joinEventButton.setOnClickListener(view -> joinEvent(view, eventID, userID));
                    checkGeolocationEnabled(eventID);

                    // Configure the long-click listener to leave the event
                    joinEventButton.setOnLongClickListener(view -> {
                        promptLeaveEvent(view, eventID, userID);
                        return true;
                    });
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
     * Sets up a popup dialog with additional event options.
     */
    private void showPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.event_overlay);
        dialog.show();
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

    /**
     * Joins the event by adding the user ID to the entrant and waiting lists in Firestore.
     *
     * @param view   the view triggering the action
     * @param eventID the event ID
     * @param userID  the user ID
     */
    private void joinEvent(View view, String eventID, String userID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String entrantsString = document.getString("entrants");
                    entrantsNum = entrantsString != null && !entrantsString.isEmpty()
                            ? Integer.parseInt(entrantsString)
                            : Integer.MAX_VALUE; // No restriction if entrants is empty or null
                    List<String> entrantList = (List<String>) document.get("entrantList.EntrantList");
                    entrantListSize = (entrantList != null) ? entrantList.size() : 0;

                    if (entrantListSize <= (entrantsNum - 1)) {
                        eventRef.update("entrantList.EntrantList", FieldValue.arrayUnion(userID))
                                .addOnSuccessListener(aVoid -> showJoinSuccess(view))
                                .addOnFailureListener(e -> showJoinFailure(view, e));

                        eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                                .addOnSuccessListener(aVoid -> showJoinSuccess(view))
                                .addOnFailureListener(e -> showJoinFailure(view, e));
                    } else {
                        Snackbar.make(view, "Waiting list is full. Try again later.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
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

        eventRef.update("entrantList.EntrantList", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> showLeaveSuccess(view))
                .addOnFailureListener(e -> showLeaveFailure(view, e));

        eventRef.update("entrantList.Waiting", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> showLeaveSuccess(view))
                .addOnFailureListener(e -> showLeaveFailure(view, e));
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
    }

    /**
     * Shows a success message when the user successfully joins the event.
     *
     * @param view the view triggering the action
     */
    private void showJoinSuccess(View view) {
        Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
        joinEventButton.setText("Leave");
        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.leaveevent_icon, 0, 0, 0);
        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiEmphasis)));
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
        joinEventButton.setText("Join");
        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.joinevent_icon, 0, 0, 0);
        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiAccent)));
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
