package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EntrantListActivity extends AppCompatActivity {
    private static final String TAG = "EntrantListActivity";

    private RecyclerView entrantRecyclerView;
    private EntrantListAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_list);

        // Initialize RecyclerView
        entrantRecyclerView = findViewById(R.id.entrantRecyclerView);
        entrantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore and the adapter with an empty list
        db = FirebaseFirestore.getInstance();
        adapter = new EntrantListAdapter(this, new ArrayList<>());
        entrantRecyclerView.setAdapter(adapter);

        // Load entrant list from Firebase
        loadEntrantsFromFirebase();
    }

    private void loadEntrantsFromFirebase() {
        String eventId = getIntent().getStringExtra("eventId"); // Retrieve event ID from intent

        if (eventId == null) {
            Log.e(TAG, "Event ID is null");
            Toast.makeText(this, "Error: Event ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> entrants = (List<String>) documentSnapshot.get("entrantList.EntrantList");
                        if (entrants != null) {
                            adapter.updateEntrantList(entrants); // Update the list in the adapter
                        } else {
                            Log.d(TAG, "No entrants found.");
                            Toast.makeText(this, "No entrants found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Event not found");
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading entrants", e);
                    Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                });
    }
}
