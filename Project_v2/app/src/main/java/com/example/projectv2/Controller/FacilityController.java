/**
 * FacilityController manages the creation, addition, and retrieval of facilities
 * stored in Firestore. It provides methods for creating a new facility, adding it to
 * Firestore, and fetching the list of facilities for use in the application.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.example.projectv2.Model.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Controller for handling facility-related operations in Firestore.
 * This class provides methods to create, add, and retrieve facilities.
 */
public class FacilityController {
    private final FirebaseFirestore db;
    private final ArrayList<Facility> facilityList = new ArrayList<>();
    private final Context context;

    /**
     * Callback interface for facility-related operations to communicate results
     * back to the calling class.
     */
    public interface FacilityCallback {
        void onFacilityListLoaded(ArrayList<Facility> facilities);
        void onError(Exception e);
    }

    /**
     * Constructs a FacilityController with the given context, initializing the Firestore
     * database instance.
     *
     * @param context the context in which this controller operates
     */
    public FacilityController(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new facility with the specified details and saves it to Firestore.
     *
     * @param name        the name of the facility
     * @param description a description of the facility
     * @param id          the unique ID of the facility
     * @param callback    callback to handle success or error
     */
    public void createFacility(String owner, String name, String description, String id, FacilityCallback callback) {
        Facility newFacility = new Facility(owner, name, description, id);
        addFacilityToFirestore(newFacility, callback);
    }

    /**
     * Adds a facility to the Firestore database.
     *
     * @param facility the Facility object to add to Firestore
     * @param callback callback to handle success or error
     */
    public void addFacilityToFirestore(Facility facility, FacilityCallback callback) {
        db.collection("facilities")
                .add(facility)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FacilityController", "Facility added with ID: " + documentReference.getId());
                    facilityList.add(facility); // Optionally add to the local list
                    callback.onFacilityListLoaded(facilityList); // Notify callback with updated list
                })
                .addOnFailureListener(e -> {
                    Log.w("FacilityController", "Error adding facility", e);
                    callback.onError(e);
                });
    }

    /**
     * Fetches the list of all the user's facilities from Firestore and notifies the callback with the
     * facility data.
     *
     * @param callback callback to handle the loaded facilities or errors
     */
    public void fetchUserFacilities(FacilityCallback callback) {
        @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("fetchCreatedEvents", "DeviceID => " + deviceID);
        db.collection("facilities")
                .whereEqualTo("owner", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            @SuppressLint("HardwareIds")
                            String ownerID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String id = document.getId(); // Get document ID

                            Facility facility = new Facility(ownerID, name, description, id);
                            facilityList.add(facility);
                        }
                        callback.onFacilityListLoaded(facilityList);
                    } else {
                        Log.w("FacilityController", "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Fetches the list of all facilities from Firestore and notifies the callback with the
     * facility data.
     *
     * @param callback callback to handle the loaded facilities or errors
     */
    public void fetchAllFacilities(FacilityCallback callback) {
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            @SuppressLint("HardwareIds")
                            String ownerID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String id = document.getId(); // Get document ID

                            Facility facility = new Facility(ownerID, name, description, id);
                            facilityList.add(facility);
                        }
                        callback.onFacilityListLoaded(facilityList);
                    } else {
                        Log.w("FacilityController", "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }
}
