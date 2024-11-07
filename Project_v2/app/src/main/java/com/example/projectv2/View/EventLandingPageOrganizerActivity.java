// EventLandingPageActivity.java is the Page which displays the information about the event when clicked on in homescreen.xml

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
import com.example.projectv2.View.QrOrganiserActivity;

public class EventLandingPageOrganizerActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventNameView, eventDetailsView, eventRulesView, eventDeadlineView, eventPriceView, eventCountdownView;
    private Button qrcodeButton; // Button for generating a QR code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_organiser);

        topBarUtils.topBarSetup(this, "Event", View.VISIBLE);

        // Initialize views
        qrcodeButton = findViewById(R.id.qrcode_button);
        eventImageView = findViewById(R.id.event_picture);
        eventNameView = findViewById(R.id.event_name_view);
        eventDetailsView = findViewById(R.id.event_details_view_organiser);
        eventRulesView = findViewById(R.id.event_rules_view);
        eventDeadlineView = findViewById(R.id.event_deadline_view);
        eventCountdownView = findViewById(R.id.event_countdown_view);
        eventPriceView = findViewById(R.id.event_price_view);

        // Retrieve event data from intent and provide fallback values
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String imageUriString = intent.getStringExtra("imageUri");
        String eventID = intent.getStringExtra("eventID");
        String owner = intent.getStringExtra("owner");
        Event event = (Event) intent.getSerializableExtra("event");

        // Configure QR Code button
        qrcodeButton.setOnClickListener(v -> {
            Intent qrIntent = new Intent(EventLandingPageOrganizerActivity.this, QrOrganiserActivity.class);
            qrIntent.putExtra("description", details);
            qrIntent.putExtra("posterUrl", imageUriString);
            qrIntent.putExtra("eventID", eventID);
            startActivity(qrIntent);
        });

        Button viewEntrantListButton = findViewById(R.id.view_entrant_list_button);
        viewEntrantListButton.setOnClickListener(v -> {
            Intent entrantListIntent = new Intent(EventLandingPageOrganizerActivity.this, EntrantListActivity.class);
            entrantListIntent.putExtra("eventId", getIntent().getStringExtra("eventID")); // Pass the eventID to EntrantListActivity
            startActivity(entrantListIntent);
        });

        // Set UI components with event data, or fallback if null
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
        dialog.setContentView(R.layout.event_edit_overlay);
        dialog.show();
    }
}