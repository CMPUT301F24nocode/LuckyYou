/**
 * Activity that displays detailed information about an event when selected from the home screen.
 * Provides options for viewing entrant lists, generating QR codes, and additional event options.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * EventLandingPageOrganizerActivity displays event details for organizers and provides
 * options to view entrants, generate QR codes, and access additional event settings.
 */
public class EventLandingPageOrganizerActivity extends AppCompatActivity {
    private static final String TAG = "EventLandingPage";
    private ImageButton eventEditPoster;
    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView;
    private Button qrcodeButton;
    private String eventName;

    /**
     * Called when the activity is created. Sets up UI elements with event data and
     * provides buttons for accessing QR codes, entrant lists, and additional settings.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_organizer);

        topBarUtils.topBarSetup(this, "Event", View.VISIBLE);

        // Initialize SwipeRefreshLayout
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {

            reloadEventData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Initialize views
        eventEditPoster=findViewById(R.id.event_edit_button);
        qrcodeButton = findViewById(R.id.qrcode_button);
        eventImageView = findViewById(R.id.event_picture_organiser);
        eventNameView = findViewById(R.id.event_name_view_organiser);
        eventDetailsView = findViewById(R.id.event_details_view_organiser);
        eventRulesView = findViewById(R.id.event_rules_view_organiser);
        eventDeadlineView = findViewById(R.id.event_deadline_view_organiser);
        eventCountdownView = findViewById(R.id.event_countdown_view_organiser);
        eventPriceView = findViewById(R.id.event_price_view_organiser);

        Button locationButton = findViewById(R.id.location_button);
        locationButton.setVisibility(View.GONE);

        // Retrieve event data from intent
        Intent intent = getIntent();
        eventName = intent.getStringExtra("name");
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String imageUriString = intent.getStringExtra("imageUri");
        String eventID = intent.getStringExtra("eventID");

        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Invalid event name", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadEventPoster(eventName);

        // Fetch geolocationenabled value and set location button visibility
        fetchGeolocationEnabled(eventID, locationButton);

        // Configure QR Code button to navigate to QrOrganiserActivity
        qrcodeButton.setOnClickListener(v -> {
            Intent qrIntent = new Intent(EventLandingPageOrganizerActivity.this, QrOrganiserActivity.class);
            qrIntent.putExtra("eventID", eventID);
            qrIntent.putExtra("name", name);
            startActivity(qrIntent);
        });

        // Configure Location button to navigate to LocationActivity
        locationButton.setOnClickListener(v -> {
            Intent locationIntent = new Intent(EventLandingPageOrganizerActivity.this, LocationActivity.class);
            locationIntent.putExtra("eventId", eventID);
            startActivity(locationIntent);
        });

        // Configure button to navigate to EntrantListActivity with eventID
        Button viewEntrantListButton = findViewById(R.id.view_entrant_list_button);
        viewEntrantListButton.setOnClickListener(v -> {
            Intent entrantListIntent = new Intent(EventLandingPageOrganizerActivity.this, EntrantListActivity.class);
            entrantListIntent.putExtra("eventId", eventID);
            entrantListIntent.putExtra("name", name);
            startActivity(entrantListIntent);
        });

        // Set UI components with event data or default values if data is missing
        eventNameView.setText(name != null ? name : "No name");
        eventDetailsView.setText(details != null ? details : "No details");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");

        // Set up event edit button
        eventEditPoster.setOnClickListener(v -> {
            Intent editIntent = new Intent(EventLandingPageOrganizerActivity.this, EventEditActivity.class);
            editIntent.putExtra("name", eventName);
            editIntent.putExtra("details", details);
            editIntent.putExtra("rules", rules);
            editIntent.putExtra("deadline", deadline);
            editIntent.putExtra("startDate", startDate);
            editIntent.putExtra("price", price);
            editIntent.putExtra("eventID", eventID);
            startActivity(editIntent);
        });

        // Load event image if URI is available; otherwise, use a placeholder image
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

        // Configure more options button to show a popup
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup(eventID, eventName));
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
                Log.e("EventLandingPage", "Failed to load image for event: " + eventName, e);
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }
    /**
     * Displays a popup dialog with additional event options.
     */
    private void showPopup(String eventId, String eventName) {
        ChooseAttendeeActivity dialogFragment = ChooseAttendeeActivity.newInstance(this, eventId, eventName);
        dialogFragment.show(getSupportFragmentManager(), "EventEditDialogFragment");
    }

    /**
     * Fetches the `geolocationenabled` value from Firestore and sets the visibility of the Location button.
     *
     * @param eventID       The event ID to fetch data for.
     * @param locationButton The Location button to show/hide.
     */
    private void fetchGeolocationEnabled(String eventID, Button locationButton) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocationEnabled");
                        if (geolocationEnabled != null && geolocationEnabled) {
                            locationButton.setVisibility(View.VISIBLE); // Show the button
                            Log.d(TAG, "Geolocation enabled: Showing location button.");
                        } else {
                            locationButton.setVisibility(View.GONE); // Hide the button
                            Log.d(TAG, "Geolocation disabled: Hiding location button.");
                        }
                    } else {
                        Log.w(TAG, "Event document not found.");
                        locationButton.setVisibility(View.GONE); // Hide the button as a fallback
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching geolocationenabled value", e);
                    locationButton.setVisibility(View.GONE); // Hide the button as a fallback
                });
    }

    private void reloadEventData() {
        Log.d("EventLandingPage", "Refreshing event data...");
        loadEventPoster(eventName);
    }
}

