package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FacilityLandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_landing_page);
        topBarUtils.topBarSetup(this, "Facility", View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize views
        TextView facilityNameTextView = findViewById(R.id.event_name_view);
        TextView facilityDescriptionTextView = findViewById(R.id.facility_description_view);

        // Get data from the intent
        String facilityName = getIntent().getStringExtra("facility_name");
        String facilityDescription = getIntent().getStringExtra("facility_description");
        String facilityID = getIntent().getStringExtra("facility_id");

        // Set data to views
        facilityNameTextView.setText(facilityName);
        facilityDescriptionTextView.setText(facilityDescription);

        // FacilityLandingPageActivity.java
        ImageButton editButton = findViewById(R.id.facility_edit_button);
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(FacilityLandingPageActivity.this, FacilityEditActivity.class);
            intent.putExtra("facilityID", facilityID);  // Pass the document ID
            startActivity(intent);
        });


        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup());
    }

    private void showPopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.facility_admin_overlay);
        dialog.show();
    }
}