// FacilityController.java
package com.example.projectv2.Controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.projectv2.Model.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FacilityController {
    private FirebaseFirestore db;
    private final ArrayList<Facility> facilityList = new ArrayList<>();

    public interface FacilityCallback {
        void onFacilityListLoaded(ArrayList<Facility> facilities);
        void onError(Exception e);
    }

    public FacilityController(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    // Method to create a Facility and store it in Firebase
    public void createFacility(String name, String description, String id, FacilityCallback callback) {
        // Create a new Facility object
        Facility newFacility = new Facility(name, description, id);

        // Add the new facility to Firebase
        addFacilityToFirestore(newFacility, callback);
    }

    // Method to add a facility to Firestore
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

    // Fetch facilities from Firestore and notify callback
    public void fetchFacilities(FacilityCallback callback) {
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String id = document.getId(); // Get document ID

                            Facility facility = new Facility(name, description, id);
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