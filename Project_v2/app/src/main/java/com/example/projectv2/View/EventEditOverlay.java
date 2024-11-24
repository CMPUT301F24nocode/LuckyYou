package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.EntrantListController;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventEditOverlay extends AppCompatActivity {

    private EntrantListController entrantListController;
    private FirebaseFirestore db;
    private int attendeesLimit;
    String eventID, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_overlay);

        entrantListController = new EntrantListController(); // Initialize the controller
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        Button chooseAttendeesButton = findViewById(R.id.choose_attendees_button);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventId");
        userID = intent.getStringExtra("user");

        chooseAttendeesButton.setOnClickListener(view -> setSelectedList(view, eventID, userID));
    }

    private void setSelectedList(View view, String eventID, String userID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    attendeesLimit = Integer.parseInt(document.getString("attendees"));
                    // Retrieve the waiting list
                    List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                    if (waitingList != null && !waitingList.isEmpty()) {
                        Collections.shuffle(waitingList);

                        // Select the attendees based on the limit
                        List<String> selectedAttendees = waitingList.size() <= attendeesLimit
                                ? new ArrayList<>(waitingList)
                                : waitingList.subList(0, attendeesLimit);

                        // Add selected attendees to the "Selected" list and remove them from "Waiting"
                        eventRef.update("entrantList.Selected", FieldValue.arrayUnion(selectedAttendees.toArray()))
                                .addOnSuccessListener(aVoid -> {
                                    for (String attendee : selectedAttendees) {
                                        eventRef.update("entrantList.Waiting", FieldValue.arrayRemove(attendee))
                                                .addOnSuccessListener(innerVoid -> Log.d("EventEditOverlay", "Attendee moved successfully: " + attendee))
                                                .addOnFailureListener(e -> Log.e("EventEditOverlay", "Error removing attendee: ", e));
                                    }
                                    Toast.makeText(EventEditOverlay.this, "Attendees chosen successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EventEditOverlay.this, "Failed to update selected attendees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("EventEditOverlay", "Error updating selected list: ", e);
                                });
                    } else {
                        Toast.makeText(EventEditOverlay.this, "No users in the waiting list to select.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventEditOverlay.this, "Event not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EventEditOverlay.this, "Failed to fetch event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
