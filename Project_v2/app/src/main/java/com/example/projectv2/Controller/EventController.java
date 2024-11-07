package com.example.projectv2.Controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.example.projectv2.Model.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EventController {
    private FirebaseFirestore db;
    private final ArrayList<Event> eventList = new ArrayList<>();

    public interface EventCallback {
        void onEventListLoaded(ArrayList<Event> events);

        void onEventCreated(String eventId);

        void onError(Exception e);
    }

    public EventController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    public interface ImageUpdateCallback {
        void onComplete(boolean success);
    }

    // Method to create an Event and store it in Firebase
    public void createEvent(String name, String detail, String rules, String deadline, String attendees, String entrants,
                            String startDate, String ticketPrice, boolean geolocationEnabled, boolean notificationsEnabled,
                            Uri selectedImageUri, String facility, EventCallback callback) {
        // Generate a random 7-digit event ID
        Random random = new Random();
        String eventID = String.valueOf(1000000 + random.nextInt(9000000)); // Generates a 7-digit number as a String

        // Create a map to represent the event and the entrant list fields
        Map<String, Object> eventMap = new HashMap<>();
        Log.d("EventController", "Event Map: " + eventMap);
        eventMap.put("name", name);
        eventMap.put("detail", detail);
        eventMap.put("rules", rules);
        eventMap.put("deadline", deadline);
        eventMap.put("attendees", attendees);
        eventMap.put("entrants", entrants);
        eventMap.put("startDate", startDate);
        eventMap.put("ticketPrice", ticketPrice);
        eventMap.put("geolocationEnabled", geolocationEnabled);
        eventMap.put("notificationsEnabled", notificationsEnabled);
        eventMap.put("imageUri", selectedImageUri != null ? selectedImageUri.toString() : null);
        eventMap.put("facility", facility);
        eventMap.put("eventID", eventID);

        // Add empty lists for entrant subfields
        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("Attendees", new ArrayList<>());
        entrantListMap.put("Unlucky", new ArrayList<>());
        entrantListMap.put("Declined", new ArrayList<>());
        entrantListMap.put("Removed", new ArrayList<>());
        entrantListMap.put("EntrantList", new ArrayList<>());
        eventMap.put("entrantList", entrantListMap);

        // Use the eventID as the document ID in Firestore
        db.collection("events").document(eventID)
                .set(eventMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event with entrant list added successfully with ID: " + eventID);
                    callback.onEventCreated(eventID); // Pass the custom eventID back to the callback
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error adding event with entrant list: " + e.getMessage());
                    callback.onError(e);
                });
    }

    public void updateEventImage(String eventId, Uri newImageUri, ImageUpdateCallback callback) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("imageUri", newImageUri.toString());

        db.collection("events").document(eventId)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Fetch events from Firestore and notify callback
    public void fetchEvents(EventCallback callback) {
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String owner = document.getString("owner");
                            String name = document.getString("name");
                            String detail = document.getString("detail");
                            String rules = document.getString("rules");
                            String facility = document.getString("facility");
                            String deadline = document.getString("deadline");
                            String startDate = document.getString("startDate");
                            String ticketPrice = document.getString("ticketPrice");
                            String eventID = document.getString("eventID");
                            Uri imageUri = document.getString("imageUri") != null ? Uri.parse(document.getString("imageUri")) : null;

                            // Creating Event object with all fields including imageUri
                            Event event = new Event(owner, name, detail, rules, deadline, startDate, ticketPrice, imageUri, facility);
                            eventList.add(event);
                        }
                        callback.onEventListLoaded(eventList);
                    } else {
                        Log.w("EventController", "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    public void checkAndAddEntrant(String eventId, String name, String email, String phoneNumber, EventCallback callback) {
        // Fetch the event document from Firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the current entrant list and the limit
                        List<Map<String, String>> currentEntrantList = (List<Map<String, String>>) documentSnapshot.get("entrantList.EntrantList");
                        int entrantsLimit = documentSnapshot.getLong("entrants").intValue(); // Ensure 'entrants' is stored as a number

                        // Ensure currentEntrantList is initialized to avoid NullPointerException
                        if (currentEntrantList == null) {
                            currentEntrantList = new ArrayList<>();
                        }

                        // Check if the current number of entrants is below the limit
                        if (currentEntrantList.size() < entrantsLimit) {
                            // Create a map to represent the new entrant's details
                            Map<String, String> userDetails = new HashMap<>();
                            userDetails.put("name", name);
                            userDetails.put("email", email);
                            userDetails.put("phoneNumber", phoneNumber);

                            // Add the user details to the EntrantList
                            db.collection("events").document(eventId)
                                    .update("entrantList.EntrantList", FieldValue.arrayUnion(userDetails))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("EventController", "User with details added to EntrantList successfully.");
                                        callback.onEventCreated(eventId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("EventController", "Error adding user to EntrantList: " + e.getMessage());
                                        callback.onError(e);
                                    });
                        } else {
                            // Deny the request if the limit is reached
                            Log.d("EventController", "Entrant limit reached. No more users can join.");
                            callback.onError(new Exception("Entrant limit reached. No more users can join."));
                        }
                    } else {
                        Log.e("EventController", "Event not found.");
                        callback.onError(new Exception("Event not found."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error fetching event data: " + e.getMessage());
                    callback.onError(e);
                });
    }
    public void updateEventQrHash(String eventId, String qrHash) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("qrHash", qrHash);

        db.collection("events").document(eventId)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("EventController", "QR hash updated successfully"))
                .addOnFailureListener(e -> Log.e("EventController", "Error updating QR hash", e));
    }
}
