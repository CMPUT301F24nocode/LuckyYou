//package com.example.projectv2.View;
//
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.projectv2.R;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.List;
//
//public class EventEditOverlay extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//    private Button chooseAttendee;
//    private int attendeeNum;
//    private int attendeeListSize;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.event_edit_overlay);
//
//        db = FirebaseFirestore.getInstance();
//        chooseAttendee = findViewById(R.id.choose_attendees_button);
//
//        Intent intent = getIntent();
//        String eventID = intent.getStringExtra("eventID");
//
//        chooseAttendee.setOnClickListener(view -> {
//            DocumentReference eventRef = db.collection("events").document(eventID);
//            eventRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        // Get the 'entrantsNum' field and store it in a variable
//                        attendeeNum = Integer.parseInt(document.getString("attendees"));
//
//                        List<String> attendeeList = (List<String>) document.get("entrantList.Attendee");
//                        if (attendeeList != null) {
//                            attendeeListSize = attendeeList.size();
//                            Log.d("Firestore", "Number of entries in EntrantList: " + attendeeListSize);
//                        }
//                        // Log or use the variable as needed
//                        Log.d("Firestore", "Entrants: " + attendeeNum);
//
//                        if (attendeeListSize <= attendeeNum) {
//                            eventRef.update("entrantList.EntrantList", FieldValue.arrayUnion(userID))
//                                    .addOnSuccessListener(aVoid -> {
//                                        // Success feedback
//                                        Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Error feedback
//                                        Snackbar.make(view, "Failed to join event: " + e.getMessage(),
//                                                Snackbar.LENGTH_LONG).show();
//                                        e.printStackTrace();
//                                    });
//
//                            eventRef.update("entrantList.Waiting", FieldValue.arrayUnion(userID))
//                                    .addOnSuccessListener(aVoid -> {
//                                        // Success feedback
//                                        Snackbar.make(view, "Successfully joined the event!", Snackbar.LENGTH_LONG).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Error feedback
//                                        Snackbar.make(view, "Failed to join event: " + e.getMessage(),
//                                                Snackbar.LENGTH_LONG).show();
//                                        e.printStackTrace();
//                                    });
//                        }
//                        else{
//                            Snackbar.make(view, "Waiting list is full. Try again later.", Snackbar.LENGTH_LONG).show();
//                        }
//                    }
//                }
//            });
//        });
//    }
//}