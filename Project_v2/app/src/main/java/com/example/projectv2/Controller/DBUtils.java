package com.example.projectv2.Controller;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class DBUtils {
    FirebaseFirestore db;

    public DBUtils() {
        db = FirebaseFirestore.getInstance();
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
}