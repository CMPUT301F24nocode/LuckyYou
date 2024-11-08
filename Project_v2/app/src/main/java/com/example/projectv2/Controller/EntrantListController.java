package com.example.projectv2.Controller;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantListController {

    private FirebaseFirestore db;

    public EntrantListController() {
        db = FirebaseFirestore.getInstance();
    }

    public void addEntrantListField(String eventId) {
        // Create empty arrays for each subfield
        List<String> emptyList = new ArrayList<>(); // Modifiable lists

        // Create a map to represent the entrantList field with empty arrays
        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("EntrantList", emptyList);
        entrantListMap.put("WaitingList", emptyList);
        entrantListMap.put("SelectedList", emptyList);
        entrantListMap.put("CancelledList", emptyList);
        entrantListMap.put("AttendeeList", emptyList);

        // Add or update the map in the Firestore document with merge option
        db.collection("events").document(eventId)
                .set(new HashMap<String, Object>() {{
                    put("entrantList", entrantListMap);
                }}, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Entrant list field with empty arrays added successfully!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding entrant list field: " + e.getMessage());
                });
    }

    public void updateSelectedList(String eventId, List<String> selectedList) {
        db.collection("events").document(eventId)
                .update("entrantList.SelectedList", selectedList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Selected List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Selected List", e));
    }

    public void updateCancelledList(String eventId, List<String> cancelledList) {
        db.collection("events").document(eventId)
                .update("entrantList.CancelledList", cancelledList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Cancelled List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Cancelled List", e));
    }

    public void updateAttendeeList(String eventId, List<String> attendeeList) {
        db.collection("events").document(eventId)
                .update("entrantList.AttendeeList", attendeeList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Attendee List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Attendee List", e));
    }

    public void addToCancelledList(String eventId, String userId) {
        db.collection("events").document(eventId)
                .update("entrantList.CancelledList", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "User added to Cancelled List"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error adding to Cancelled List", e));
    }
}