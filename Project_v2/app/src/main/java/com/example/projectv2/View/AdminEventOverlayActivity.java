package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class AdminEventOverlayActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;
    private String eventName;
    private FirebaseStorage storage;
    private ImageController imageController;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_overlay);

        // Setup top bar
        topBarUtils.topBarSetup(this, "Event", View.INVISIBLE);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        imageController = new ImageController();
        notificationService = new NotificationService();

        // Retrieve the eventID and eventName from the intent
        eventID = getIntent().getStringExtra("eventID");
        eventName = getIntent().getStringExtra("name");

        // Set up delete button listener
        Button deleteEventButton = findViewById(R.id.delete_event_button);
        deleteEventButton.setOnClickListener(v -> deleteEvent());

        // Set up delete QR data button listener
        Button deleteQrDataButton = findViewById(R.id.delete_event_qrdata_button);
        deleteQrDataButton.setOnClickListener(v -> deleteQrHashData());
    }

    private void deleteEvent() {
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String owner = documentSnapshot.getString("owner");
                        db.collection("events").document(eventID)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AdminEventOverlay", "Event successfully deleted!");
                                    sendOwnerNotification(owner, "Your event, " + eventName + ", has been deleted by an admin.");
                                    Toast.makeText(this, "Event deleted successfully!", Toast.LENGTH_SHORT).show();

                                    if (eventName != null && !eventName.isEmpty()) {
                                        String sanitizedEventName = sanitizeEventName(eventName);
                                        String posterPath = "event_posters/event_posters_" + sanitizedEventName + ".jpg";
                                        deleteEventPoster(posterPath);
                                    } else {
                                        finish(); // Close the activity
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AdminEventOverlay", "Error deleting event", e);
                                    Toast.makeText(this, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Event not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventOverlay", "Error fetching event data", e);
                });
    }

    private void deleteQrHashData() {
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String owner = documentSnapshot.getString("owner");
                        db.collection("events").document(eventID)
                                .update("qrHashData", FieldValue.delete())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AdminEventOverlay", "QR Hash Data successfully deleted!");
                                    sendOwnerNotification(owner, "The QR hash data for your event, " + eventName + ", has been deleted by an admin.");
                                    Toast.makeText(this, "QR Hash Data deleted successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AdminEventOverlay", "Error deleting QR Hash Data", e);
                                    Toast.makeText(this, "Failed to delete QR Hash Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Event not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventOverlay", "Error fetching event data", e);
                });
    }

    private void sendOwnerNotification(String ownerID, String message) {
        if (ownerID != null && !ownerID.isEmpty()) {
            Notification notification = new Notification(ownerID, message, false, true);
            notificationService.sendNotification(this, notification, "-1");
        } else {
            Log.w("AdminEventOverlay", "Owner ID is null or empty. Cannot send notification.");
        }
    }

    @SuppressLint("RestrictedApi")
    private void deleteEventPoster(String filePath) {
        Log.d("AdminEventOverlay", "Attempting to delete poster at: " + filePath);

        imageController.deleteImage(filePath, new ImageController.ImageDeleteCallback() {
            @Override
            public void onDeleteSuccess() {
                Log.d("AdminEventOverlay", "Event poster successfully deleted!");
                Toast.makeText(AdminEventOverlayActivity.this, "Event poster deleted successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }

            @Override
            public void onDeleteFailure(Exception e) {
                Log.e("AdminEventOverlay", "Error deleting event poster", e);
                finish(); // Close the activity even if poster deletion fails
            }
        });
    }

    private String sanitizeEventName(String eventName) {
        return eventName.trim()
                .replaceAll("[/\\-?!@#$%^&*()]+", "_") // Replace special characters with underscores
                .replaceAll("\\s+", "_"); // Replace spaces with underscores
    }
}
