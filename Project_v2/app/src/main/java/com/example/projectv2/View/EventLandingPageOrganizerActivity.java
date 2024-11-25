package com.example.projectv2.View;

import android.content.Intent;
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
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.R;

public class EventLandingPageOrganizerActivity extends AppCompatActivity {

    private static final String TAG = "EventLandingPage";
    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView;
    private ImageButton eventEditPoster;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_organiser);

        // Initialize views
        eventImageView = findViewById(R.id.event_picture_organiser);
        eventNameView = findViewById(R.id.event_name_view_organiser);
        eventDetailsView = findViewById(R.id.event_details_view_organiser);
        eventRulesView = findViewById(R.id.event_rules_view_organiser);
        eventDeadlineView = findViewById(R.id.event_deadline_view_organiser);
        eventPriceView = findViewById(R.id.event_price_view_organiser);
        eventCountdownView = findViewById(R.id.event_countdown_view_organiser);
        eventEditPoster = findViewById(R.id.event_edit_button);

        // Retrieve event data from intent
        Intent intent = getIntent();
        eventName = intent.getStringExtra("name"); // Event name
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String eventID = intent.getStringExtra("eventID");

        // Validate event name
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Invalid event name", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Log the event name for debugging
        Log.d(TAG, "Event Name: " + eventName);

        // Set data to views
        eventNameView.setText(eventName);
        eventDetailsView.setText(details != null ? details : "No details available");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline specified");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");

        // Load the event poster image
        loadEventPoster(eventName);

        // Set up the edit poster button
        eventEditPoster.setOnClickListener(v -> {
            Intent editIntent = new Intent(EventLandingPageOrganizerActivity.this, EventEditActivity.class);
            editIntent.putExtra("eventName", eventName);
            editIntent.putExtra("details", details);
            editIntent.putExtra("rules", rules);
            editIntent.putExtra("deadline", deadline);
            editIntent.putExtra("startDate", startDate);
            editIntent.putExtra("price", price);
            editIntent.putExtra("eventID", eventID);
            startActivity(editIntent);
        });
    }

    /**
     * Load the event poster using the ImageController.
     *
     * @param eventName The name of the event.
     */
    private void loadEventPoster(String eventName) {
        ImageController imageController = new ImageController();

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                // Use Glide to load the image into the ImageView
                Glide.with(EventLandingPageOrganizerActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                        .error(R.drawable.placeholder_event) // Placeholder if loading fails
                        .centerCrop()
                        .into(eventImageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                // Log the error and show the placeholder image
                Log.e(TAG, "Failed to load image for event: " + eventName, e);
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }
}