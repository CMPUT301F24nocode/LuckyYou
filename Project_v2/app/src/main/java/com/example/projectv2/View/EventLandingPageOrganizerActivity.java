/**
 * Activity that displays detailed information about an event when selected from the home screen.
 * Provides options for viewing entrant lists, generating QR codes, and additional event options.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

/**
 * EventLandingPageOrganizerActivity displays event details for organizers and provides
 * options to view entrants, generate QR codes, and access additional event settings.
 */
public class EventLandingPageOrganizerActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView;
    private ImageButton eventEditPoster;
    private Button qrcodeButton;

    /**
     * Called when the activity is created. Sets up UI elements with event data and
     * provides buttons for accessing QR codes, entrant lists, and additional settings.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_organiser);

        topBarUtils.topBarSetup(this, "Event", View.VISIBLE);

        // Initialize views
        eventImageView = findViewById(R.id.event_picture_organiser);
        eventEditPoster = findViewById(R.id.event_edit_button);

        // Retrieve event data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name"); // Event name
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String eventID = intent.getStringExtra("eventID");

        // Load the event poster image
        loadEventPoster(name);

        // Set up event edit button
        eventEditPoster.setOnClickListener(v -> {
            Intent editIntent = new Intent(EventLandingPageOrganizerActivity.this, EventEditActivity.class);
            editIntent.putExtra("name", name);
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
                Log.e("EventLandingPage", "Failed to load image for event: " + eventName, e);
                eventImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }
}
