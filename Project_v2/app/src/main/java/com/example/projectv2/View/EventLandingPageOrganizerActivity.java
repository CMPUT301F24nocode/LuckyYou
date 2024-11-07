package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;



public class EventLandingPageOrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_landing_page_organiser);

        topBarUtils.topBarSetup(this, "Event", View.VISIBLE);

        // Initialize views
        ImageView eventImageView = findViewById(R.id.event_picture_organiser);
        TextView eventNameView = findViewById(R.id.event_name_view_organiser);
        TextView eventDetailsView = findViewById(R.id.event_details_view_organiser);
        TextView eventRulesView = findViewById(R.id.event_rules_view_organiser);
        TextView eventDeadlineView = findViewById(R.id.event_deadline_view_organiser);
        TextView eventCountdownView = findViewById(R.id.event_countdown_view_organiser);
        TextView eventPriceView = findViewById(R.id.event_price_view_organiser);

        // Retrieve event data from intent and provide fallback values
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String rules = intent.getStringExtra("rules");
        String deadline = intent.getStringExtra("deadline");
        String startDate = intent.getStringExtra("startDate");
        String price = intent.getStringExtra("price");
        String imageUriString = intent.getStringExtra("imageUri");


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
        dialog.setContentView(R.layout.event_edit_overlay);
        dialog.show();
    }
}