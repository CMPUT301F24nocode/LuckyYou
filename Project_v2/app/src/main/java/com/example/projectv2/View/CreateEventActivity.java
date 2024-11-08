/**
 * Activity for creating a new event. Allows users to enter event details such as name, details,
 * rules, facility, and an optional image. Users can also proceed to configure additional event options.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
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

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateEventActivity allows users to input basic details for creating a new event.
 * Users can provide event name, details, rules, and select a facility or image for the event.
 * The activity also loads facilities from Firebase and lets users proceed to additional event configuration options.
 */
public class CreateEventActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EVENT_OPTIONS = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;

    private EditText eventNameView, eventDetailsView, eventRulesView;
    private ImageView eventImageView;
    private Spinner facilitySpinner;
    private Uri selectedImageUri;
    private List<String> facilityList = new ArrayList<>();
    private FirebaseFirestore db;

    /**
     * Called when the activity is created. Sets up the UI elements for inputting event details
     * and configures the top bar and facility spinner. Allows image selection and navigating to
     * additional event options.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
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

        topBarUtils.topBarSetup(this, "Create Event", View.INVISIBLE);

        // Set up the ImageButton to open the gallery for selecting an image
        ImageButton imageButton = findViewById(R.id.create_event_pic_button);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        // Set up the Next button to open CreateEventOptionsActivity
        Button nextButton = findViewById(R.id.create_event_next_button);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateEventActivity.this, CreateEventOptionsActivity.class);
            intent.putExtra("name", eventNameView.getText().toString());
            intent.putExtra("detail", eventDetailsView.getText().toString());
            intent.putExtra("rules", eventRulesView.getText().toString());
            String selectedFacility = facilitySpinner.getSelectedItem() != null ? facilitySpinner.getSelectedItem().toString() : "Online";
            intent.putExtra("facility", selectedFacility);

            if (selectedImageUri != null) {
                intent.putExtra("imageUri", selectedImageUri.toString());
            }

            startActivityForResult(intent, REQUEST_CODE_EVENT_OPTIONS);
        });
    }

    /**
     * Loads the list of available facilities from Firebase Firestore and populates the facility spinner.
     * Adds "Online" as a default facility option.
     */
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

    /**
     * Handles results from activities launched for result. Handles image selection for the event
     * and passing event data back from the event options configuration.
     *
     * @param requestCode the request code used when starting the activity
     * @param resultCode  the result code returned by the activity
     * @param data        the Intent containing the result data from the launched activity
     */
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
