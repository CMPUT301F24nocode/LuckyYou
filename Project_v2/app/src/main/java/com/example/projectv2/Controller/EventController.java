// EventController.java
package com.example.projectv2.Controller;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.projectv2.Model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // Method to create an Event and store it in Firebase
    public void createEvent(String name, String detail, String rules, String deadline, String attendees, String entrants,
                            String startDate, String ticketPrice, boolean geolocationEnabled, boolean notificationsEnabled,
                            Uri imageUri, EventCallback callback) {
        // Create an Event object or use a map to store event details
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("name", name);
        eventDetails.put("detail", detail);
        eventDetails.put("rules", rules);
        eventDetails.put("deadline", deadline);
        eventDetails.put("attendees", attendees);
        eventDetails.put("entrants", entrants);
        eventDetails.put("startDate", startDate);
        eventDetails.put("ticketPrice", ticketPrice);
        eventDetails.put("geolocationEnabled", geolocationEnabled);
        eventDetails.put("notificationsEnabled", notificationsEnabled);
        eventDetails.put("imageUri", imageUri != null ? imageUri.toString() : null);

        db.collection("events").add(eventDetails)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    callback.onEventCreated(eventId);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }

    // Method to add an event to Firestore
    public void addEventToFirestore(Event event, EventCallback callback) {
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EventController", "Event added with ID: " + documentReference.getId());
                    eventList.add(event); // Optionally add to the local list
                    callback.onEventListLoaded(eventList); // Notify callback with updated list
                })
                .addOnFailureListener(e -> {
                    Log.w("EventController", "Error adding event", e);
                    callback.onError(e);
                });
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
                            String deadline = document.getString("deadline");
                            String startDate = document.getString("startDate");
                            String ticketPrice = document.getString("ticketPrice");
                            Uri imageUri = document.getString("imageUri") != null ? Uri.parse(document.getString("imageUri")) : null;

                            Event event = new Event(name, detail, rules, deadline, startDate, ticketPrice, imageUri);
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
