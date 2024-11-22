package com.example.projectv2.Controller;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing entrant lists in the Firestore database.
 * This class provides methods to create, update, and modify various entrant lists
 * for a specified event.
 */
public class EntrantListController {

    private final FirebaseFirestore db;

    /**
     * Constructs an EntrantListController and initializes the Firestore database instance.
     */
    public EntrantListController() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds an entrant list field with empty arrays for EntrantList, Waiting, Selected, Cancelled, and Attendee
     * to the Firestore document of the specified event.
     *
     * @param eventId the ID of the event to which the entrant list field will be added
     */
    public void addEntrantListField(String eventId) {
        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("EntrantList", new ArrayList<>());
        entrantListMap.put("Waiting", new ArrayList<>());
        entrantListMap.put("Selected", new ArrayList<>());
        entrantListMap.put("Cancelled", new ArrayList<>());
        entrantListMap.put("Attendee", new ArrayList<>());

        db.collection("events").document(eventId)
                .set(new HashMap<String, Object>() {{
                    put("entrantList", entrantListMap);
                }}, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Entrant list field added successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error adding entrant list field: " + e.getMessage()));
    }

    public void fetchEvent(String eventId, OnFetchEventListener listener) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> listener.onSuccess(documentSnapshot))
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Listener interface for event fetch operations.
     */
    public interface OnFetchEventListener {
        void onSuccess(DocumentSnapshot documentSnapshot);

        void onFailure(Exception e);
    }

    /**
     * Adds a specified user ID to a given list (e.g., Waiting, Selected) in Firestore.
     *
     * @param eventId the ID of the event
     * @param listName the name of the list to update (e.g., "Selected", "Waiting")
     * @param userId  the user ID to add to the list
     */
    public void addUserToList(String eventId, String listName, String userId) {
        db.collection("events").document(eventId)
                .update("entrantList." + listName, FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "User added to " + listName + " successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error adding user to " + listName + ": " + e.getMessage()));
    }

    /**
     * Removes a specified user ID from a given list (e.g., Waiting) in Firestore.
     *
     * @param eventId the ID of the event
     * @param listName the name of the list to update (e.g., "Waiting", "Cancelled")
     * @param userId  the user ID to remove from the list
     */
    public void removeUserFromList(String eventId, String listName, String userId) {
        db.collection("events").document(eventId)
                .update("entrantList." + listName, FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "User removed from " + listName + " successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error removing user from " + listName + ": " + e.getMessage()));
    }

    /**
     * Updates a specific list in Firestore with a new set of values.
     * This method overwrites the entire list, so it should be used cautiously.
     *
     * @param eventId    the ID of the event
     * @param listName   the name of the list to update (e.g., "Selected", "Waiting")
     * @param updatedList the new list of users to replace the existing list
     */
    public void updateList(String eventId, String listName, List<String> updatedList) {
        db.collection("events").document(eventId)
                .update("entrantList." + listName, updatedList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", listName + " updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating " + listName + ": " + e.getMessage()));
    }

    /**
     * Initializes a missing list in Firestore if it doesn't exist.
     *
     * @param eventId the ID of the event
     * @param listName the name of the list to initialize
     */
    public void initializeListIfMissing(String eventId, String listName) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.contains("entrantList." + listName)) {
                        db.collection("events").document(eventId)
                                .update("entrantList." + listName, new ArrayList<>())
                                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", listName + " initialized successfully"))
                                .addOnFailureListener(e -> Log.e("EntrantListController", "Error initializing " + listName + ": " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error checking list existence: " + e.getMessage()));
    }

    /**
     * Moves a user from one list to another (e.g., from Waiting to Selected).
     *
     * @param eventId the ID of the event
     * @param fromList the name of the source list
     * @param toList   the name of the destination list
     * @param userId   the user ID to move
     */
    public void moveUserBetweenLists(String eventId, String fromList, String toList, String userId) {
        removeUserFromList(eventId, fromList, userId);
        addUserToList(eventId, toList, userId);
    }
}
