// EventController.java
package com.example.projectv2.Controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.projectv2.Model.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventController {
    private FirebaseFirestore db;
    private final ArrayList<Event> eventList = new ArrayList<>();

    public interface EventCallback {
        void onEventListLoaded(List<Event> events);
        void onEventListLoaded(ArrayList<Event> events);
        void onError(Exception e);
    }

    public EventController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    // Method to create an Event and store it in Firebase
    public void createEvent(String name, String detail, String rules, String deadline, String attendees, String entrants, String startDate, String ticketPrice, boolean geolocationEnabled, boolean notificationsEnabled, Uri selectedImageUri, EventCallback callback) {
        // Create a new Event object
        Event newEvent = new Event(name, detail, rules, deadline, startDate, ticketPrice, selectedImageUri);

        // Add the new event to Firebase
        addEventToFirestore(newEvent, callback);
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

    // Method to delete an event image reference from Firestore
    public void deleteEventImage(String eventId, EventCallback callback) {
        // Remove the image URL from the event document in Firestore
        db.collection("events").document(eventId)
                .update("imageUri", FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Image reference removed from Firestore.");
                    fetchEvents(callback); // Refresh the event list
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error removing image reference from Firestore", e);
                    callback.onError(e);
                });
    }

    // Method to add an entrant to the waiting list of an event
    public void joinWaitingList(String eventId, String userId, EventCallback callback) {
        db.collection("events").document(eventId)
                .update("waitingList", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "User added to waiting list.");
                    fetchEvents(callback); // Fetch updated list
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error adding to waiting list", e);
                    callback.onError(e);
                });
    }

    // Method to remove an entrant from the waiting list of an event
    public void leaveWaitingList(String eventId, String userId, EventCallback callback) {
        db.collection("events").document(eventId)
                .update("waitingList", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "User removed from waiting list.");
                    fetchEvents(callback); // Fetch updated list
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error removing from waiting list", e);
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


