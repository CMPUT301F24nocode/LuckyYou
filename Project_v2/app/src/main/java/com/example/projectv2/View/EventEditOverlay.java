package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.EntrantListController;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventEditOverlay extends AppCompatActivity {

    private FirebaseFirestore db;
    private int attendeeNum;
    private int attendeeListSize;
    private int remainingSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_overlay);

        db = FirebaseFirestore.getInstance();
        Button chooseAttendee = findViewById(R.id.choose_attendees_button);

        Intent intent = getIntent();
//        String eventID = intent.getStringExtra("eventID");
        String eventID = intent.getStringExtra("1104124");
//        String userID=intent.getStringExtra("user");


        chooseAttendee.setOnClickListener(view -> {
            DocumentReference eventRef = db.collection("events").document(eventID);
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("fbkjdcsnv", "Doc success?");
                    if (document.exists()) {
                        Log.d("fbkjdcsnv", "Doc exists");
                        // Get the 'entrantsNum' field and store it in a variable
                        attendeeNum = Integer.parseInt(document.getString("attendees"));

                        List<String> attendeeList = (List<String>) document.get("entrantList.Attendee");
                        if (attendeeList != null) {
                            attendeeListSize = attendeeList.size();
                            Log.d("fbkjdcsnv", "Number of entries in AttendeeList: " + attendeeListSize);
                        }
                        // Log or use the variable as needed
                        Log.d("fbkjdcsnv", "Entrants: " + attendeeNum);

                        remainingSlots = attendeeNum - attendeeListSize;

                        List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                        if (waitingList != null && !waitingList.isEmpty()) {
                            Collections.shuffle(waitingList);
                            List<String> selectedList = waitingList.size() > remainingSlots ? waitingList.subList(0, remainingSlots) : waitingList;
//                            EntrantListController controller = new EntrantListController();
//                            controller.updateSelectedList(eventID, selectedList);

                            // Set the data in Firestore
                            eventRef.update("entrantList.Selected", selectedList)
                                    .addOnSuccessListener(aVoid -> {
                                        // Document successfully written
                                        Log.d("fbkjdcsnv", "DocumentSnapshot successfully written!");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the error
                                        Log.w("fbkjdcsnv", "Error writing document", e);
                                    });

                        }

                        // Choose attendeeNum - attendeeSize (remaining slots) of deviceIDs from waiting list and put them in Selected.
                        // Send notification to them all
                        // If anyone accepts, send them to attendee and reduce size of remaining slots
                        // If anyone declines, send them to cancelled, remove them from waiting list, and pick another deviceID
                    }
                }
            });
        });
    }
}