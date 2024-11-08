package com.example.projectv2.View;

import static android.app.ProgressDialog.show;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


public class EventLandingPageUserActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView;
    private Button joinEventButton;
    private FirebaseFirestore db;

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

        // Retrieve event data from intent and provide fallback values
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String imageUriString = intent.getStringExtra("imageUri");
        String userID=intent.getStringExtra("user");
        String eventID=intent.getStringExtra("eventID");
        Event event = (Event) intent.getSerializableExtra("event");


        joinEventButton.setOnClickListener(view -> {
            Log.d("bgvefdfvjghbbhdvfbdhfvhbvfdbhfvdbhgjdfv", "ITS REACHING HUZZAH");
                DocumentReference eventRef = db.collection("events").document(eventID);
                eventRef.update("entrantList.EntrantList", FieldValue.arrayUnion(userID))
                        .addOnSuccessListener(aVoid -> {
                            // Success feedback
                            Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
                            joinEventButton.setEnabled(false);
                        joinEventButton.setText("Leave");
                        joinEventButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.leaveevent_icon,0,0,0);
                    joinEventButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lucky_uiEmphasis)));
                    })
                    .addOnFailureListener(e -> {
                        // Error feedback
                            Snackbar.make(view, "Failed to join event: " + e.getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    });
        });

        joinEventButton.setOnLongClickListener(view -> {
//                joinEventButton.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(EventLandingPageUserActivity.this);
                builder.setTitle("Leave Event");
                builder.setMessage("Are you sure you want to leave this event?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    DocumentReference eventRef = db.collection("events").document(eventID);
                    eventRef.update("entrantList.EntrantList", FieldValue.arrayRemove(userID))
                            .addOnSuccessListener(aVoid -> {

                                Snackbar.make(view, "Successfully left the event", Snackbar.LENGTH_LONG).show();
                                joinEventButton.setEnabled(true);
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(view, "Failed to leave event: " + e.getMessage(),
                                        Snackbar.LENGTH_LONG).show();
                                joinEventButton.setEnabled(true);
                                e.printStackTrace();
                            });});
                return true;
            });


        // Set data to views with null-checks
        eventNameView.setText(name != null ? name : "No name");
        eventDetailsView.setText(details != null ? details : "No details");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");
        // Load image if URI is available
        try {
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriString);
                eventImageView.setImageURI(imageUri); // Attempt to load directly
            } else {
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            eventImageView.setImageResource(R.drawable.placeholder_event); // Fallback if loading fails
        }

        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup());
    }

    private void showPopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.event_overlay);
        dialog.show();
    }
}