package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.MainActivity;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventCreatorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EVENT_OPTIONS = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;

    private EditText eventNameView, eventDetailsView, eventRulesView;
    private ImageView eventImageView;
    private Spinner facilitySpinner;
    private Uri selectedImageUri; // Store the image URI as a field
    private List<String> facilityList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventNameView = findViewById(R.id.create_event_name_view);
        eventDetailsView = findViewById(R.id.create_event_details_view);
        eventRulesView = findViewById(R.id.create_event_rules_view);
        eventImageView = findViewById(R.id.create_event_pic_view);
        facilitySpinner = findViewById(R.id.facility_spinner);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        loadFacilities();

        // Set up the Back button to return to MainActivity (homescreen)
        ImageButton backButton = findViewById(R.id.event_back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up the ImageButton to open the gallery for selecting an image
        ImageButton imageButton = findViewById(R.id.create_event_pic_button);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        // Set up the Next button to open EventOptionsActivity
        Button nextButton = findViewById(R.id.create_event_next_button);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatorActivity.this, CreateEventOptionsActivity.class);
            intent.putExtra("name", eventNameView.getText().toString());
            intent.putExtra("detail", eventDetailsView.getText().toString());
            intent.putExtra("rules", eventRulesView.getText().toString());
            // Get the selected facility or set to "Online" by default
            String selectedFacility = facilitySpinner.getSelectedItem() != null ? facilitySpinner.getSelectedItem().toString() : "Online";
            intent.putExtra("facility", selectedFacility);

            // Include the image URI if an image was selected
            if (selectedImageUri != null) {
                intent.putExtra("imageUri", selectedImageUri.toString());
            }

            startActivityForResult(intent, REQUEST_CODE_EVENT_OPTIONS);
        });
    }

    private void loadFacilities() {
        db.collection("facilities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityName = document.getString("name");
                    if (facilityName != null) {
                        facilityList.add(facilityName);
                    }
                }
                facilityList.add(0, "Online");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facilityList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                facilitySpinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Error loading facilities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                eventImageView.setImageURI(selectedImageUri);
            }
        } else if (requestCode == REQUEST_CODE_EVENT_OPTIONS && resultCode == RESULT_OK) {
            String name = eventNameView.getText().toString();
            String detail = eventDetailsView.getText().toString();
            String rules = eventRulesView.getText().toString();
            String facility = facilitySpinner.getSelectedItem().toString();


            if (data != null) {
                data.putExtra("name", name);
                data.putExtra("detail", detail);
                data.putExtra("rules", rules);
                data.putExtra("facility", facility);

                if (selectedImageUri != null) {
                    data.putExtra("imageUri", selectedImageUri.toString());
                }
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}