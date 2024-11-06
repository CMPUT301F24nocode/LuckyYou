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
        int eventID = 1000000 + random.nextInt(9000000); // Generates a number between 1000000 and 9999999


        // Create a map to represent the event and the entrant list fields
        Map<String, Object> eventMap = new HashMap<>();
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
        eventMap.put("eventID", String.valueOf(eventID)); // Add the random eventID

        db.collection("events").add(eventMap)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    Log.d("EventController", "Event with eventID " + eventID + " added successfully with Firestore ID: " + eventId);
                    callback.onEventCreated(eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error adding event with eventID " + eventID + ": " + e.getMessage());
                    callback.onError(e);
                });


        // Add empty lists for entrant subfields
        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("Attendees", new ArrayList<>());
        entrantListMap.put("Unlucky", new ArrayList<>());
        entrantListMap.put("Declined", new ArrayList<>());
        entrantListMap.put("Removed", new ArrayList<>());
        entrantListMap.put("EntrantList", new ArrayList<>());
        eventMap.put("entrantList", entrantListMap);


        // Add the event and the entrant list fields to Firestore
        db.collection("events").add(eventMap)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    Log.d("EventController", "Event with entrant list added successfully with ID: " + eventId);
                    callback.onEventCreated(eventId);
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
                .addOnSuccessListener(aVoid -> callback.onComplete(true))    // Pass `true` to indicate success
                .addOnFailureListener(e -> callback.onComplete(false));      // Pass `false` to indicate failure
    }


    // Fetch events from Firestore and notify callback
    public void fetchEvents(EventCallback callback) {
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String detail = document.getString("detail");
                            String rules = document.getString("rules");
                            String facility = document.getString("facility");
                            String deadline = document.getString("deadline");
                            String startDate = document.getString("startDate");
                            String ticketPrice = document.getString("ticketPrice");
                            Uri imageUri = document.getString("imageUri") != null ? Uri.parse(document.getString("imageUri")) : null;


                            Event event = new Event(name, detail, rules, deadline, startDate, ticketPrice, imageUri, facility);
                            eventList.add(event);
                        }
                        callback.onEventListLoaded(eventList);
                    } else {
                        Log.w("EventController", "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }
}