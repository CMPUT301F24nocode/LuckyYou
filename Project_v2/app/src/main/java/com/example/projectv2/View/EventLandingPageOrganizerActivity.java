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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
        qrcodeButton = findViewById(R.id.qrcode_button);
        eventImageView = findViewById(R.id.event_picture_organiser);
        eventNameView = findViewById(R.id.event_name_view_organiser);
        eventDetailsView = findViewById(R.id.event_details_view_organiser);
        eventRulesView = findViewById(R.id.event_rules_view_organiser);
        eventDeadlineView = findViewById(R.id.event_deadline_view_organiser);
        eventCountdownView = findViewById(R.id.event_countdown_view_organiser);
        eventPriceView = findViewById(R.id.event_price_view_organiser);

        // Retrieve event data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String imageUriString = intent.getStringExtra("imageUri");
        String eventID = intent.getStringExtra("eventID");

        // Configure QR Code button to navigate to QrOrganiserActivity
        qrcodeButton.setOnClickListener(v -> {
            Intent qrIntent = new Intent(EventLandingPageOrganizerActivity.this, QrOrganiserActivity.class);
            qrIntent.putExtra("description", details);
            qrIntent.putExtra("posterUrl", imageUriString);
            qrIntent.putExtra("eventID", eventID);
            startActivity(qrIntent);
        });

        // Configure button to navigate to EntrantListActivity with eventID
        Button viewEntrantListButton = findViewById(R.id.view_entrant_list_button);
        viewEntrantListButton.setOnClickListener(v -> {
            Intent entrantListIntent = new Intent(EventLandingPageOrganizerActivity.this, EntrantListActivity.class);
            entrantListIntent.putExtra("eventId", eventID);
            startActivity(entrantListIntent);
        });

        // Set UI components with event data or default values if data is missing
        eventNameView.setText(name != null ? name : "No name");
        eventDetailsView.setText(details != null ? details : "No details");
        eventRulesView.setText(rules != null ? rules : "No rules provided");
        eventDeadlineView.setText(deadline != null ? deadline : "No deadline");
        eventCountdownView.setText(startDate != null ? "Starts in: " + startDate : "No start date");
        eventPriceView.setText(price != null && !price.equals("0") ? "$" + price : "Free");

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
        moreButton.setOnClickListener(v -> showPopup());
    }

    /**
     * Displays a popup dialog with additional event options.
     */
    private void showPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.event_edit_overlay);
        dialog.show();
    }
}
