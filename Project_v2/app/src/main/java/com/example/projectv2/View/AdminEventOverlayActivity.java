package com.example.projectv2.View;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdminEventOverlayActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;
    private String eventName;
    private FirebaseStorage storage;
    private ImageController imageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_overlay);

        // Setup top bar
        topBarUtils.topBarSetup(this, "Event", View.INVISIBLE);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        imageController = new ImageController();


        // Retrieve the eventID from the intent
        eventID = getIntent().getStringExtra("eventID");
        eventName=getIntent().getStringExtra("name");

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
                    if (eventName != null && !eventName.isEmpty()) {
                        String posterPath = "event_posters/event_posters_" + eventName + ".jpg";
                        deleteEventPoster(posterPath); // Efficiently delete the image
                    } else {
                        Log.d("AdminEventOverlay", "No event name provided, skipping poster deletion.");
                        finish(); // Close the activity
                    }
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
    @SuppressLint("RestrictedApi")
    private void deleteEventPoster(String filePath) {
        Log.d(TAG, "Attempting to delete poster at: " + filePath);

        imageController.deleteImage(filePath, new ImageController.ImageDeleteCallback() {
            @Override
            public void onDeleteSuccess() {
                Log.d(TAG, "Event poster successfully deleted!");
                Toast.makeText(AdminEventOverlayActivity.this, "Event poster deleted successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }

            @Override
            public void onDeleteFailure(Exception e) {
                Log.e(TAG, "Error deleting event poster", e);
                Toast.makeText(AdminEventOverlayActivity.this, "Failed to delete event poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish(); // Close the activity even if poster deletion fails
            }
        });
    }
}
