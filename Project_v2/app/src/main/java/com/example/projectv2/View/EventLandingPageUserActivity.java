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

import com.bumptech.glide.Glide;
import com.example.projectv2.Controller.DBUtils;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EventLandingPageUserActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView, geolocationWarningView;
    private Button joinEventButton;
    private FirebaseFirestore db;
    private String eventID, name, details, rules, deadline, startDate, price, imageUriString, userID;
    private int entrantsNum, entrantListSize;

    DBUtils dbUtils = new DBUtils();

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
        eventID = intent.getStringExtra("eventID");
        fetchEventDetails(eventID);

        // Configure the join event button
        joinEventButton.setOnClickListener(view -> joinEvent(view, eventID, userID));

        // Configure the long-click listener to leave the event
        joinEventButton.setOnLongClickListener(view -> {
            promptLeaveEvent(view, eventID, userID);
            return true;
        });

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

                runOnUiThread(() -> {
                    setEventData(name, details, rules, deadline, startDate, price, imageUriString);
                    joinEventButton.setOnClickListener(view -> joinEvent(view, eventID, userID));
                    checkGeolocationEnabled(eventID);
                    joinEventButton.setOnLongClickListener(view -> {
                        promptLeaveEvent(view, eventID, userID);
                        return true;
                    });
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void setEventData(String name, String details, String rules, String deadline, String startDate, String price, String imageUriString) {
        eventNameView.setText(name != null ? name : "No name");
        eventDetailsView.setText(details != null ? details : "No details");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");

        if (imageUriString != null && !imageUriString.isEmpty()) {
            Glide.with(this)
                    .load(imageUriString)
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .centerCrop()
                    .into(eventImageView);
        } else {
            eventImageView.setImageResource(R.drawable.placeholder_event);
        }
    }

    private void showPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.event_overlay);

        Button deleteEventButton = dialog.findViewById(R.id.delete_event_button);
        deleteEventButton.setOnClickListener(v -> deleteEvent(eventID));

        Button deleteQRCodeButton = dialog.findViewById(R.id.delete_event_qrdata_button);
        deleteQRCodeButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete QR Code")
                .setMessage("Are you sure you want to remove the QR code data for this event?")
                .setPositiveButton("Yes", (dialogInterface, which) -> deleteQRCode(eventID))
                .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss())
                .show());

        dialog.show();
    }

    private void deleteEvent(String eventID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteQRCode(String eventID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.update("hashedQRCode", FieldValue.delete())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "QR code data removed successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove QR code data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkGeolocationEnabled(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.getBoolean("geolocationEnabled") != null) {
                geolocationWarningView.setVisibility(documentSnapshot.getBoolean("geolocationEnabled") ? View.VISIBLE : View.GONE);
            }
        }).addOnFailureListener(e -> Log.e("EventLandingPageUser", "Error checking geolocation: ", e));
    }

    private void joinEvent(View view, String eventID, String userID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                List<String> waitingList = (List<String>) task.getResult().get("entrantList.Waiting");
                entrantListSize = (waitingList != null) ? waitingList.size() : 0;
                entrantsNum = task.getResult().getString("entrants") != null
                        ? Integer.parseInt(task.getResult().getString("entrants"))
                        : Integer.MAX_VALUE;

                if (entrantListSize <= (entrantsNum - 1)) {
                    eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
                            .addOnSuccessListener(aVoid -> showJoinSuccess(view))
                            .addOnFailureListener(e -> showJoinFailure(view, e));
                } else {
                    Snackbar.make(view, "Waiting list is full. Try again later.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void promptLeaveEvent(View view, String eventID, String userID) {
        new AlertDialog.Builder(this)
                .setTitle("Leave Event")
                .setMessage("Are you sure you want to leave this event?")
                .setPositiveButton("Yes", (dialog, which) -> leaveEvent(view, eventID, userID))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void leaveEvent(View view, String eventID, String userID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.update("entrantList.Waiting", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> showLeaveSuccess(view))
                .addOnFailureListener(e -> showLeaveFailure(view, e));
    }

    private void showJoinSuccess(View view) {
        Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
        joinEventButton.setText("Leave");
        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.leaveevent_icon, 0, 0, 0);
        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiEmphasis)));
    }

    private void showJoinFailure(View view, Exception e) {
        Snackbar.make(view, "Failed to join event: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    private void showLeaveSuccess(View view) {
        Snackbar.make(view, "Successfully left the event", Snackbar.LENGTH_LONG).show();
        joinEventButton.setText("Join");
        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.joinevent_icon, 0, 0, 0);
        joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiAccent)));
    }

    private void showLeaveFailure(View view, Exception e) {
        Snackbar.make(view, "Failed to leave event: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}}