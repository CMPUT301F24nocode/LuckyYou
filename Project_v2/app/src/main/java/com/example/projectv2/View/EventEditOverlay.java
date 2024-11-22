package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.EntrantListController;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventEditOverlay extends AppCompatActivity {

    private EntrantListController entrantListController;
    private int attendeesLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_overlay);

        entrantListController = new EntrantListController(); // Initialize the controller
        Button chooseAttendeesButton = findViewById(R.id.choose_attendees_button);

        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventId");

        chooseAttendeesButton.setOnClickListener(view -> {
            entrantListController.fetchEvent(eventID, new EntrantListController.OnFetchEventListener() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        attendeesLimit = documentSnapshot.getLong("attendees").intValue();

                        // Retrieve the waiting list
                        List<String> waitingList = (List<String>) documentSnapshot.get("entrantList.Waiting");
                        if (waitingList != null && !waitingList.isEmpty()) {
                            Collections.shuffle(waitingList);

                            List<String> selectedAttendees = waitingList.size() <= attendeesLimit
                                    ? new ArrayList<>(waitingList)
                                    : waitingList.subList(0, attendeesLimit);

                            Log.d("EventEditOverlay", "Selected Attendees: " + selectedAttendees);

                            // Use EntrantListController to update Firestore
                            for (String attendee : selectedAttendees) {
                                entrantListController.addUserToList(eventID, "Selected", attendee);
                                entrantListController.removeUserFromList(eventID, "Waiting", attendee);
                            }

                            Toast.makeText(EventEditOverlay.this, "Attendees chosen successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EventEditOverlay.this, "No users in the waiting list to select.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EventEditOverlay.this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EventEditOverlay.this, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                    Log.e("EventEditOverlay", "Error fetching event: " + e.getMessage());
                }
            });
        });
    }
}