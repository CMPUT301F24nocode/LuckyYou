package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;

import com.example.projectv2.Model.Notification;
import com.example.projectv2.Controller.NotificationAdapter;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying notifications to the user in a RecyclerView.
 * Retrieves notifications for a specified user from Firestore and displays them in a list.
 */
public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity"; // Tag for logging
    private FirebaseFirestore db; // Firestore instance for database operations
    private List<Notification> notificationList; // List of notifications to display
    private NotificationAdapter adapter; // Adapter for managing notifications in RecyclerView

    /**
     * Called when the activity is first created.
     * Sets up the RecyclerView and loads notifications for the specified user.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        RecyclerView recyclerView = findViewById(R.id.notification_recylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications("12345"); // Figure out who to display notifications for
    }

    /**
     * Loads notifications for a specific user from Firestore and updates the RecyclerView.
     * Retrieves notifications for the user based on their role (admin or organiser).
     *
     * @param userId The ID of the user whose notifications are to be loaded
     */
    private void loadNotifications(String userId) {
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        Boolean isOrganiser = documentSnapshot.getBoolean("organiser");

                        if (Boolean.TRUE.equals(isAdmin)) {
                            List<Map<String, Object>> adminNotifs = (List<Map<String, Object>>) documentSnapshot.get("admin_notif");
                            if (adminNotifs != null) {
                                for (Map<String, Object> notifData : adminNotifs) {
                                    Notification notification = mapToNotification(notifData, true, false);
                                    notificationList.add(notification);
                                }
                            }
                        }

                        if (Boolean.TRUE.equals(isOrganiser)) {
                            List<Map<String, Object>> orgNotifs = (List<Map<String, Object>>) documentSnapshot.get("organiser_notif");
                            if (orgNotifs != null) {
                                for (Map<String, Object> notifData : orgNotifs) {
                                    Notification notification = mapToNotification(notifData, false, true);
                                    notificationList.add(notification);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "No such document.");
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error fetching document: ", e));
    }

    /**
     * Maps a Firestore document's data to a Notification object.
     *
     * @param notifData   The map containing notification data retrieved from Firestore
     * @param isAdmin     True if the notification is for admins, false otherwise
     * @param isOrganiser True if the notification is for organisers, false otherwise
     * @return A new Notification object with data populated from the map
     */
    private Notification mapToNotification(Map<String, Object> notifData, boolean isAdmin, boolean isOrganiser) {
        String sendTo = (String) notifData.get("sendTo");
        String content = (String) notifData.get("content");
        String timeSent = (String) notifData.get("timeSent");

        Notification notification = new Notification(sendTo, content, timeSent, isOrganiser, isAdmin);
        return notification;
    }
}
