package com.example.projectv2.View;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.R;

public class FacilityLandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_landing_page);

        // Back button to navigate back to the previous activity
        ImageButton eventBackButton = findViewById(R.id.facility_back_button);
        eventBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Ends the current activity and returns to the previous screen
            }
        });

        ImageView facilityImageView = findViewById(R.id.facility_picture);
        TextView facilityNameTextView = findViewById(R.id.event_name_view);
        TextView facilityDescriptionTextView = findViewById(R.id.facility_description_view);

        // Get data from intent
        String facilityName = getIntent().getStringExtra("facility_name");
        String facilityDescription = getIntent().getStringExtra("facility_description");
        String imageUriString = getIntent().getStringExtra("facility_image");

        // Set data to views
        facilityNameTextView.setText(facilityName);
        facilityDescriptionTextView.setText(facilityDescription);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            facilityImageView.setImageURI(imageUri);
        } else {
            facilityImageView.setImageResource(R.drawable.placeholder_event); // Default image
        }
    }
}
