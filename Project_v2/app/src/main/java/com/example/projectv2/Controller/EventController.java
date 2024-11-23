/**
 * EventController manages events in the Firebase Firestore database.
 * It supports creating, updating, and fetching events, as well as handling event images
 * and managing the entrant lists for each event.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
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

/**
 * Controller for handling event-related operations in Firestore, including
 * event creation, updating, fetching, and entrant list management.
 */
public class EventController {
    private final FirebaseFirestore db;
    private final ArrayList<Event> eventList = new ArrayList<>();

    /**
     * Callback interface for event-related operations to communicate results
     * back to the calling class.
     */
    public interface EventCallback {
        void onEventListLoaded(ArrayList<Event> events);
        void onEventCreated(String eventId);
        void onError(Exception e);
    }

    /**
     * Constructs an EventController with the specified context, initializing
     * the Firestore database instance.
     *
     * @param context the context in which this controller operates
     */
    public EventController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for image update operations, to indicate completion status.
     */
    public interface ImageUpdateCallback {
        void onComplete(boolean success);
    }

    /**
     * Creates a new event with the specified details and saves it to Firestore.
     * The event is stored with a randomly generated event ID and includes an entrant list.
     *
     * @param name                 the name of the event
     * @param detail               details of the event
     * @param rules                rules of the event
     * @param deadline             deadline of the event
     * @param attendees            number of attendees
     * @param entrants             number of entrants allowed
     * @param startDate            start date of the event
     * @param ticketPrice          ticket price of the event
     * @param geolocationEnabled   if geolocation is enabled for the event
     * @param notificationsEnabled if notifications are enabled for the event
     * @param selectedImageUri     URI of the selected image for the event
     * @param facility             facility information for the event
     * @param callback             callback to handle success or error
     */
    public void createEvent(String name, String detail, String rules, String deadline, String attendees, String entrants,
                            String startDate, String ticketPrice, boolean geolocationEnabled, boolean notificationsEnabled,
                            Uri selectedImageUri, String facility, EventCallback callback) {
        Random random = new Random();
        String eventID = String.valueOf(1000000 + random.nextInt(9000000));

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

        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("Waiting", new ArrayList<>());
        entrantListMap.put("Selected", new ArrayList<>());
        entrantListMap.put("Cancelled", new ArrayList<>());
        entrantListMap.put("Attendee", new ArrayList<>());
        eventMap.put("entrantList", entrantListMap);

        db.collection("events").document(eventID)
                .set(eventMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EventController", "Event with entrant list added successfully with ID: " + eventID);
                    callback.onEventCreated(eventID);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventController", "Error adding event with entrant list: " + e.getMessage());
                    callback.onError(e);
                });
    }

    /**
     * Updates the event image in Firestore.
     *
     * @param eventId      the ID of the event whose image is to be updated
     * @param newImageUri  URI of the new image to update
     * @param callback     callback to handle the update result
     */
    public void updateEventImage(String eventId, Uri newImageUri, ImageUpdateCallback callback) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("imageUri", newImageUri.toString());

        db.collection("events").document(eventId)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    /**
     * Fetches the list of events from Firestore and notifies the callback with the event data.
     *
     * @param callback callback to handle the loaded events or errors
     */
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

                            Event event = new Event(eventID, owner, name, detail, rules, deadline, startDate, ticketPrice, imageUri, facility);
                            eventList.add(event);
                        }
                        callback.onEventListLoaded(eventList);
                    } else {
                        Log.w("EventController", "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Checks if an entrant can be added to an event, and if so, adds the entrant's details.
     * If the entrant list reaches its limit, an error is reported.
     *
     * @param eventId      the ID of the event to add the entrant to
     * @param name         the name of the entrant
     * @param email        the email of the entrant
     * @param phoneNumber  the phone number of the entrant
     * @param callback     callback to handle success or error
     */
    public void checkAndAddEntrant(String eventId, String name, String email, String phoneNumber, EventCallback callback) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, String>> currentWaitingList = (List<Map<String, String>>) documentSnapshot.get("entrantList.Waiting");
                        int entrantsLimit = documentSnapshot.getLong("entrants").intValue();

                        if (currentWaitingList == null) {
                            currentWaitingList = new ArrayList<>();
                        }

                        if (currentWaitingList.size() < entrantsLimit) {
                            Map<String, String> userDetails = new HashMap<>();
                            userDetails.put("name", name);
                            userDetails.put("email", email);
                            userDetails.put("phoneNumber", phoneNumber);

                            db.collection("events").document(eventId)
                                    .update("entrantList.Waiting", FieldValue.arrayUnion(userDetails))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("EventController", "User with details added to Waiting List successfully.");
                                        callback.onEventCreated(eventId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("EventController", "Error adding user to Waiting List: " + e.getMessage());
                                        callback.onError(e);
                                    });
                        } else {
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
}
