// EventCreatorActivity.java allows users to input event details such as name, details, rules, facility, and an optional image.

package com.example.projectv2.View;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.R;
import com.example.projectv2.View.CreateEventOptionsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventCreatorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EVENT_OPTIONS = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;

    private EditText eventNameView, eventDetailsView, eventRulesView; // Text fields for input
    private ImageView eventImageView; // Image view to display selected image
    private Spinner facilitySpinner; // Spinner to select facilities
    private Uri selectedImageUri; // URI of selected image
    private List<String> facilityList = new ArrayList<>(); // Facility list for spinner
    private FirebaseFirestore db; // Firebase Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize UI components
        eventNameView = findViewById(R.id.create_event_name_view);
        eventDetailsView = findViewById(R.id.create_event_details_view);
        eventRulesView = findViewById(R.id.create_event_rules_view);
        eventImageView = findViewById(R.id.create_event_pic_view);
        facilitySpinner = findViewById(R.id.facility_spinner);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        loadFacilities(); // Load facility list from Firestore

        // Set up image button to open gallery for image selection
        ImageButton imageButton = findViewById(R.id.create_event_pic_button);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        // Set up "Next" button to navigate to EventOptionsActivity with entered data
        Button nextButton = findViewById(R.id.create_event_next_button);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatorActivity.this, CreateEventOptionsActivity.class);
            intent.putExtra("name", eventNameView.getText().toString());
            intent.putExtra("detail", eventDetailsView.getText().toString());
            intent.putExtra("rules", eventRulesView.getText().toString());
            String selectedFacility = facilitySpinner.getSelectedItem() != null ? facilitySpinner.getSelectedItem().toString() : "Online";
            intent.putExtra("facility", selectedFacility);
            if (selectedImageUri != null) intent.putExtra("imageUri", selectedImageUri.toString());
            startActivityForResult(intent, REQUEST_CODE_EVENT_OPTIONS);
        });
    }

    // Load facility list from Firestore to populate spinner
    private void loadFacilities() {
        db.collection("facilities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityName = document.getString("name");
                    if (facilityName != null) facilityList.add(facilityName);
                }
                facilityList.add(0, "Online");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facilityList);
                facilitySpinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Error loading facilities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle results from image selection and EventOptionsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) eventImageView.setImageURI(selectedImageUri);
        } else if (requestCode == REQUEST_CODE_EVENT_OPTIONS && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
