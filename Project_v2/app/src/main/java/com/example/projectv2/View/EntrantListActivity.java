package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntrantListActivity extends AppCompatActivity {
    private static final String TAG = "EntrantListActivity";

    // UI elements for displaying the entrant list and filter options
    private RecyclerView entrantRecyclerView;
    private EntrantListAdapter adapter;
    private FirebaseFirestore db; // Firebase Firestore instance for database access
    private Spinner filterSpinner; // Spinner for filter selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_list);

        // Set up the top bar with title and back button
        topBarUtils.topBarSetup(this, "Entrant List", View.INVISIBLE);

        // Initialize RecyclerView and set its layout to a vertical list
        entrantRecyclerView = findViewById(R.id.entrantRecyclerView);
        entrantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore database instance and the adapter with an empty list
        db = FirebaseFirestore.getInstance();
        adapter = new EntrantListAdapter(this, new ArrayList<>());
        entrantRecyclerView.setAdapter(adapter); // Set adapter to RecyclerView

        // Initialize the filter dropdown (Spinner)
        filterSpinner = findViewById(R.id.entrant_list_dropdown);
        setupFilterSpinner();

        // Load the full list of entrants by default
        loadEntrantList();
    }

    // Set up the filter spinner with options: "Entrant List", "Attendees", "Declined"
    private void setupFilterSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Entrant List", "Attendees", "Declined"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Handle spinner item selection to apply filtering
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Determine which filter option is selected and load the corresponding data
                String selectedOption = (String) parent.getItemAtPosition(position);
                switch (selectedOption) {
                    case "Entrant List":
                        loadEntrantList(); // Load full list of entrants
                        break;
                    case "Attendees":
                        loadAttendees(); // Load up to 20 randomly selected attendees
                        break;
                    case "Declined":
                        loadDeclined(); // Load list of declined entrants
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to showing Entrant List if nothing is selected
                loadEntrantList();
            }
        });
    }

    // Method to load the full entrant list from Firebase Firestore
    private void loadEntrantList() {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the entrant list from Firestore based on event ID
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the entire list of entrants from "entrantList.EntrantList" field
                        List<String> entrantList = (List<String>) documentSnapshot.get("entrantList.EntrantList");
                        if (entrantList != null) {
                            adapter.updateEntrantList(entrantList); // Update adapter with entrants
                        } else {
                            Toast.makeText(this, "No entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading entrant list", e);
                    Toast.makeText(this, "Failed to load entrant list", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to load a random selection of up to 20 attendees (FOR NOW)
    private void loadAttendees() {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the attendees list from Firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> attendees = (List<String>) documentSnapshot.get("entrantList.Attendees");
                        if (attendees != null) {
                            // Shuffle and select up to 20 random attendees
                            Collections.shuffle(attendees);
                            List<String> selectedAttendees = attendees.size() > 20 ? attendees.subList(0, 20) : attendees;
                            adapter.updateEntrantList(selectedAttendees); // Update adapter with selected attendees
                        } else {
                            Toast.makeText(this, "No attendees found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading attendees", e));
    }

    // Method to load a list of declined entrants from Firestore
    private void loadDeclined() {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        // Fetch the declined list from Firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> declined = (List<String>) documentSnapshot.get("entrantList.Declined");
                        if (declined != null) {
                            adapter.updateEntrantList(declined); // Update adapter with declined entrants
                        } else {
                            Toast.makeText(this, "No declined entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading declined entrants", e));
    }
}
