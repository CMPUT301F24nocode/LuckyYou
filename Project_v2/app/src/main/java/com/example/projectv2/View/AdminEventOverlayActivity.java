package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventOverlayActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_overlay);

        // Setup top bar
        topBarUtils.topBarSetup(this, "Event", View.INVISIBLE);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the eventID from the intent
        eventID = getIntent().getStringExtra("eventID");

        // Set up delete button listener
        Button deleteEventButton = findViewById(R.id.delete_event_button);
        deleteEventButton.setOnClickListener(v -> deleteEvent());

        // Set up delete QR data button listener
        Button deleteQrDataButton = findViewById(R.id.delete_event_qrdata_button);
        deleteQrDataButton.setOnClickListener(v -> deleteQrHashData());
    }

    /**
     * Deletes the event from Firestore using the eventID.
     */
    private void deleteEvent() {
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminEventOverlay", "Event successfully deleted!");
                    Toast.makeText(this, "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deletion
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventOverlay", "Error deleting event", e);
                    Toast.makeText(this, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes the qrHashData field of the event from Firestore.
     */
    private void deleteQrHashData() {
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID)
                .update("qrHashData", FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminEventOverlay", "QR Hash Data successfully deleted!");
                    Toast.makeText(this, "QR Hash Data deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventOverlay", "Error deleting QR Hash Data", e);
                    Toast.makeText(this, "Failed to delete QR Hash Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
