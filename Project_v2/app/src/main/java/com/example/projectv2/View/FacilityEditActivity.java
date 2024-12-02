package com.example.projectv2.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FacilityEditActivity extends AppCompatActivity {

    private EditText facilityNameEdit, facilityDescriptionEdit;
    private FirebaseFirestore db;
    private String facilityID;

    // Constructor for testing with dependency injection
    public FacilityEditActivity() {
        this(FirebaseFirestore.getInstance());
    }

    // Constructor that allows passing a FirebaseFirestore instance
    public FacilityEditActivity(FirebaseFirestore firestore) {
        this.db = firestore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_edit_page);

        topBarUtils.topBarSetup(this, "Edit Facility", View.INVISIBLE);

        facilityNameEdit = findViewById(R.id.facility_edit_name_view);
        facilityDescriptionEdit = findViewById(R.id.facility_edit_description_view);

        facilityID = getIntent().getStringExtra("facilityID");

        if (facilityID == null || facilityID.isEmpty()) {
            Toast.makeText(this, "Invalid Facility ID. Unable to edit.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ensure db is not null before using
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        // Fetch current data from Firestore
        DocumentReference facilityRef = db.collection("facilities").document(facilityID);
        facilityRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                facilityNameEdit.setHint(documentSnapshot.getString("name"));
                facilityDescriptionEdit.setHint(documentSnapshot.getString("description"));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load facility data", Toast.LENGTH_SHORT).show();
        });

        // Set up the save button
        Button saveButton = findViewById(R.id.facility_edit_confirm_button);
        saveButton.setOnClickListener(view -> {
            String newName = facilityNameEdit.getText().toString().trim();
            String newDescription = facilityDescriptionEdit.getText().toString().trim();

            if (newName.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Firestore
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", newName);
            updatedData.put("description", newDescription);

            db.collection("facilities").document(facilityID)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Facility updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Method for testing to set Firestore instance
    public void setFirestoreInstance(FirebaseFirestore firestore) {
        this.db = firestore;
    }
}