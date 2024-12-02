package com.example.projectv2.Controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectv2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DBUtils {
    static FirebaseFirestore db;

    public DBUtils() {
        db = FirebaseFirestore.getInstance();
    }

    public static void removeUser(String userID, String eventID) {
        db.collection("events").document(eventID)
                .get().
                addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the entrantList
                        Map<String, List<String>> entrantList = (Map<String, List<String>>) documentSnapshot.get("entrantList");
                        if (entrantList != null) {
                            // Remove userID from all sublists
                            entrantList.keySet().forEach(key -> Objects.requireNonNull(entrantList.get(key)).remove(userID));

                            // Add userID to the Cancelled sublist
                            List<String> cancelledList = entrantList.get("Cancelled");
                            if (cancelledList == null) {
                                cancelledList = new ArrayList<>();
                                entrantList.put("Cancelled", cancelledList);
                            }
                            if (!cancelledList.contains(userID)) {
                                cancelledList.add(userID);
                            }

                            // Update the event document
                            db.collection("events").document(eventID)
                                    .update("entrantList", entrantList)
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "User moved to Cancelled list successfully"))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error updating entrant list", e));
                        }
                    }
                }).addOnFailureListener(e -> Log.e("Firebase", "Error fetching event document", e));
    }

    public static void removeUsers(List<String> userIDs, String eventID) {

        db.collection("events").document(eventID)
                .get().
                addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the entrantList
                        Map<String, List<String>> entrantList = (Map<String, List<String>>) documentSnapshot.get("entrantList");
                        if (entrantList != null) {
                            for (String userID: userIDs){
                                // Remove userID from all sublists
                                entrantList.keySet().forEach(key -> Objects.requireNonNull(entrantList.get(key)).remove(userID));

                                // Add userID to the Cancelled sublist
                                List<String> cancelledList = entrantList.get("Cancelled");
                                if (cancelledList == null) {
                                    cancelledList = new ArrayList<>();
                                    entrantList.put("Cancelled", cancelledList);
                                }
                                if (!cancelledList.contains(userID)) {
                                    cancelledList.add(userID);
                                }
                            }
                            // Update the event document
                            db.collection("events").document(eventID)
                                    .update("entrantList", entrantList)
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "User moved to Cancelled list successfully"))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error updating entrant list", e));
                        }
                    }
                }).addOnFailureListener(e -> Log.e("Firebase", "Error fetching event document", e));
    }

    public interface EventCallback {
        void onCallback(HashMap<String, String> eventDetails);
    }

    public void fetchEvent(String eventID, EventCallback callback) {
        db.collection("events").document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            HashMap<String, String> eventDetails = new HashMap<>();
                            eventDetails.put("eventID", document.getString("eventID"));
                            eventDetails.put("name", document.getString("name"));
                            eventDetails.put("details", document.getString("detail"));
                            eventDetails.put("rules", document.getString("rules"));
                            eventDetails.put("deadline", document.getString("deadline"));
                            eventDetails.put("startDate", document.getString("startDate"));
                            eventDetails.put("price", document.getString("price"));
                            eventDetails.put("imageUri", document.getString("imageUri"));
                            eventDetails.put("user", document.getString("owner"));

                            callback.onCallback(eventDetails);
                        } else {
                            Log.d("DBUtils", "No such document");
                            callback.onCallback(null);
                        }
                    } else {
                        Log.d("DBUtils", "get failed with ", task.getException());
                        callback.onCallback(null);
                    }
                });
    }
    public interface UserCallback {
        void onCallback(User user);
    }

    public void fetchUser(String userID, UserCallback callback) {
        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Map the Firestore document fields to a User object
                            User user=document.toObject(User.class);
                            if (user == null) {

                            callback.onCallback(null);}
                            callback.onCallback(user);

                        } else {
                            Log.d("DBUtils", "No such document");
                            callback.onCallback(null);
                        }
                    } else {
                        Log.d("DBUtils", "get failed with ", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    public void updateUser(String userID, User user) {
        try{
        DocumentReference userRef = db.collection("Users").document(userID);
        userRef.update("profileImage", user.getProfileImage());
        }
        catch (Exception e){
            Log.d("DBUtils", "get failed with ", e);
            throw new RuntimeException(e);
        }


    }
}