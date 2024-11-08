/**
 * Activity for editing the details of an existing facility. Allows users to modify the facility's
 * name and description, then update the data in Firebase Firestore.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * FacilityEditActivity allows users to edit the name and description of a selected facility.
 * The updated data is saved to Firebase Firestore when the user confirms the changes.
 */
public class FacilityEditActivity extends AppCompatActivity {

    private EditText facilityNameEdit, facilityDescriptionEdit;
    private FirebaseFirestore db;
    private String facilityID;

    /**
     * Called when the activity is created. Sets up the UI for editing facility details
     * and retrieves the current facility data from Firestore to display as hints.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_edit_page);

        topBarUtils.topBarSetup(this, "Edit Facility", View.INVISIBLE);

        // Initialize Firestore and UI elements
        db = FirebaseFirestore.getInstance();
        facilityNameEdit = findViewById(R.id.facility_edit_name_view);
        facilityDescriptionEdit = findViewById(R.id.facility_edit_description_view);

        // Retrieve facilityID from intent
        facilityID = getIntent().getStringExtra("facilityID");

        // Fetch current data from Firestore to display in the edit fields
        DocumentReference facilityRef = db.collection("facilities").document(facilityID);
        facilityRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String description = documentSnapshot.getString("description");

                // Set current data as hints
                facilityNameEdit.setHint(name);
                facilityDescriptionEdit.setHint(description);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load facility data", Toast.LENGTH_SHORT).show();
        });

        // Set up the save button to update facility data
        Button saveButton = findViewById(R.id.facility_edit_confirm_button);
        saveButton.setOnClickListener(view -> {
            String newName = facilityNameEdit.getText().toString().trim();
            String newDescription = facilityDescriptionEdit.getText().toString().trim();

            if (newName.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a map with the new data to update in Firestore
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", newName);
            updatedData.put("description", newDescription);

            // Update the document in Firestore
            db.collection("facilities").document(facilityID)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Facility updated successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the edit page and return to the previous screen
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
