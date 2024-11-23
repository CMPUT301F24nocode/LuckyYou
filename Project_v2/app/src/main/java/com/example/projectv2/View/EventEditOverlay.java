/**
 * Activity for managing the selection of attendees from a waiting list for an event. 
 * Based on available slots, it moves a random selection of entrants from the waiting list 
 * to the selected list and updates the Firestore database accordingly.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class EventEditOverlay extends AppCompatActivity {

    private FirebaseFirestore db;
    private int attendeeNum;
    private int attendeeListSize;
    private int remainingSlots;

    /**
     * Called when the activity is created. Initializes Firebase Firestore, retrieves
     * event details from Firestore, and sets up a button to choose attendees based on
     * available slots.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_overlay);

        db = FirebaseFirestore.getInstance();
        Button chooseAttendee = findViewById(R.id.choose_attendees_button);

        Intent intent = getIntent();
        String eventID = intent.getStringExtra("1104124");  // Sample event ID for testing

        chooseAttendee.setOnClickListener(view -> {
            DocumentReference eventRef = db.collection("events").document(eventID);
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("fbkjdcsnv", "Document fetch successful");

                    if (document.exists()) {
                        Log.d("fbkjdcsnv", "Document exists in Firestore");
                        // Retrieve the 'attendees' field as a target number of attendees
                        attendeeNum = Integer.parseInt(document.getString("attendees"));

                        List<String> attendeeList = (List<String>) document.get("entrantList.Attendee");
                        if (attendeeList != null) {
                            attendeeListSize = attendeeList.size();
                            Log.d("fbkjdcsnv", "Current number of attendees: " + attendeeListSize);
                        }
                        remainingSlots = attendeeNum - attendeeListSize;

                        List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                        if (waitingList != null && !waitingList.isEmpty()) {
                            Collections.shuffle(waitingList);
                            List<String> selectedList = waitingList.size() > remainingSlots ? waitingList.subList(0, remainingSlots) : waitingList;

                            // Update the selected list in Firestore
                            eventRef.update("entrantList.Selected", selectedList)
                                    .addOnSuccessListener(aVoid -> Log.d("fbkjdcsnv", "Selected list updated successfully in Firestore"))
                                    .addOnFailureListener(e -> Log.w("fbkjdcsnv", "Error updating selected list in Firestore", e));
                        }

                        // Additional logic for attendee management, including notifications, can be added here
                    }
                } else {
                    Log.e("fbkjdcsnv", "Error fetching document", task.getException());
                }
            });
        });
    }
}
