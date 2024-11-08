/**
 * Activity that displays detailed information about a specific facility. Provides options to edit
 * facility details and access additional settings via a popup dialog.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FacilityLandingPageActivity displays information about a facility including its name and description.
 * Users can access editing options and additional settings through a popup.
 */
public class FacilityLandingPageActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. Initializes the UI elements, sets the top bar, retrieves
     * facility details from the intent, and configures the edit button and popup dialog.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_landing_page);
        topBarUtils.topBarSetup(this, "Facility", View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize views
        TextView facilityNameTextView = findViewById(R.id.event_name_view);
        TextView facilityDescriptionTextView = findViewById(R.id.facility_description_view);

        // Get facility data from the intent
        String facilityName = getIntent().getStringExtra("facility_name");
        String facilityDescription = getIntent().getStringExtra("facility_description");
        String facilityID = getIntent().getStringExtra("facility_id");

        // Set data to views
        facilityNameTextView.setText(facilityName);
        facilityDescriptionTextView.setText(facilityDescription);

        // Configure the edit button to navigate to FacilityEditActivity
        ImageButton editButton = findViewById(R.id.facility_edit_button);
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(FacilityLandingPageActivity.this, FacilityEditActivity.class);
            intent.putExtra("facilityID", facilityID);  // Pass the document ID to the edit activity
            startActivity(intent);
        });

        // Configure more settings button to display additional options in a popup dialog
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup());
    }

    /**
     * Displays a popup dialog with additional settings for the facility.
     */
    private void showPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.facility_admin_overlay);
        dialog.show();
    }
}
