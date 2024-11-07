package com.example.projectv2.View;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FacilityLandingPageActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Event> eventList = new ArrayList<>();
    private RecyclerView eventRecyclerView; // Make sure you have an adapter setup for this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_landing_page);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageButton eventBackButton = findViewById(R.id.facility_back_button);
        ImageView facilityImageView = findViewById(R.id.facility_picture);
        TextView facilityNameTextView = findViewById(R.id.event_name_view);
        TextView facilityDescriptionTextView = findViewById(R.id.facility_description_view);

        // Set up the back button
        eventBackButton.setOnClickListener(v -> finish());

        // Get data from the intent
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

        // Load events related to the facility
        loadEventsForFacility(facilityName);
    }

    private void loadEventsForFacility(String facilityName) {
        db.collection("events")
                .whereEqualTo("facilityName", facilityName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String owner = document.getString("owner");
                            String name = document.getString("name");
                            String detail = document.getString("detail");
                            Uri imageUri = document.getString("imageUri") != null ? Uri.parse(document.getString("imageUri")) : null;
                            String eventFacilityName = document.getString("facilityName");

                            Event event = new Event(null, name, detail, null, null, null, null, null, imageUri, eventFacilityName);
                            eventList.add(event);
                        }
                        // Notify your adapter of the changes, if needed
                        // eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("FacilityLandingPage", "Error getting events.", task.getException());
                        Toast.makeText(FacilityLandingPageActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
