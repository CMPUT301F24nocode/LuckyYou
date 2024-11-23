/**
 * EntrantListController is responsible for managing entrant lists for events stored in Firestore.
 * This includes creating and updating various entrant lists such as selected, cancelled, and attendee lists
 * associated with an event.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.util.Log;

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
     * Adds an entrant list field with empty arrays for EntrantList, WaitingList,
     * SelectedList, CancelledList, and AttendeeList to the Firestore document of the specified event.
     *
     * @param eventId the ID of the event to which the entrant list field will be added
     */
    public void addEntrantListField(String eventId) {
        List<String> emptyList = new ArrayList<>();

        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("EntrantList", emptyList);
        entrantListMap.put("WaitingList", emptyList);
        entrantListMap.put("SelectedList", emptyList);
        entrantListMap.put("CancelledList", emptyList);
        entrantListMap.put("AttendeeList", emptyList);

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

    /**
     * Updates the SelectedList for a specified event in the Firestore database.
     *
     * @param eventId      the ID of the event whose SelectedList will be updated
     * @param selectedList the list of selected entrants to update in the Firestore
     */
    public void updateSelectedList(String eventId, List<String> selectedList) {
        db.collection("events").document(eventId)
                .update("entrantList.Selected", selectedList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Selected List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Selected List", e));
    }

    /**
     * Updates the CancelledList for a specified event in the Firestore database.
     *
     * @param eventId       the ID of the event whose CancelledList will be updated
     * @param cancelledList the list of cancelled entrants to update in the Firestore
     */
    public void updateCancelledList(String eventId, List<String> cancelledList) {
        db.collection("events").document(eventId)
                .update("entrantList.CancelledList", cancelledList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Cancelled List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Cancelled List", e));
    }

    /**
     * Updates the AttendeeList for a specified event in the Firestore database.
     *
     * @param eventId      the ID of the event whose AttendeeList will be updated
     * @param attendeeList the list of attendees to update in the Firestore
     */
    public void updateAttendeeList(String eventId, List<String> attendeeList) {
        db.collection("events").document(eventId)
                .update("entrantList.AttendeeList", attendeeList)
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "Attendee List updated successfully"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error updating Attendee List", e));
    }

    /**
     * Adds a specified user ID to the CancelledList for a specified event in the Firestore database.
     *
     * @param eventId the ID of the event to update
     * @param userId  the user ID to add to the CancelledList
     */
    public void addToCancelledList(String eventId, String userId) {
        db.collection("events").document(eventId)
                .update("entrantList.CancelledList", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d("EntrantListController", "User added to Cancelled List"))
                .addOnFailureListener(e -> Log.e("EntrantListController", "Error adding to Cancelled List", e));
    }
}
