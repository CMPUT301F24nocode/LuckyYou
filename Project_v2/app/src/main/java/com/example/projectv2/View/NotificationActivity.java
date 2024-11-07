package com.example.projectv2.View;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Controller.topBarUtils;
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
    private FirebaseFirestore db; // FireStore instance for database operations
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

        topBarUtils.topBarSetup(this, "Notifications", View.INVISIBLE);

        RecyclerView recyclerView = findViewById(R.id.notification_recylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

//        NotificationService notificationService = new NotificationService();
//        Notification notification = new Notification("1d0e750f99dbaaab", "Org notif test", true, false);
//        notificationService.sendNotification(notification);
//        notification = new Notification("1d0e750f99dbaaab", "Admin notif test", false, true);
//        notificationService.sendNotification(notification);
//        NotificationAdapter adapter = new NotificationAdapter(notificationList);
//        recyclerView.setAdapter(adapter);

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loadNotifications("deviceID"); // Figure out who to display notifications for
    }

    /**
     * Loads notifications for a specific user from FireStore and updates the RecyclerView.
     * Retrieves notifications for the user based on their role (admin or organiser).
     *
     * @param userId The ID of the user whose notifications are to be loaded
     */
    private void loadNotifications(String userId) {
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("adminNotif");
                        Boolean isOrganiser = documentSnapshot.getBoolean("organizerNotif");

                        if (Boolean.TRUE.equals(isAdmin)) {
                            List<Map<String, Object>> adminNotifs = (List<Map<String, Object>>) documentSnapshot.get("adminNotifList");
                            if (adminNotifs != null) {
                                for (Map<String, Object> notifData : adminNotifs) {
                                    Notification notification = mapToNotification(notifData, true, false);
                                    notificationList.add(notification);
                                }
                            }
                        }

                        if (Boolean.TRUE.equals(isOrganiser)) {
                            List<Map<String, Object>> orgNotifs = (List<Map<String, Object>>) documentSnapshot.get("organizerNotifList");
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
     * Maps a FireStore document's data to a Notification object.
     *
     * @param notifData   The map containing notification data retrieved from FireStore
     * @param isAdmin     True if the notification is for admins, false otherwise
     * @param isOrganiser True if the notification is for organisers, false otherwise
     * @return A new Notification object with data populated from the map
     */
    private Notification mapToNotification(Map<String, Object> notifData, boolean isAdmin, boolean isOrganiser) {
        String sendTo = (String) notifData.get("sendTo");
        String content = (String) notifData.get("content");
        String timeSent = (String) notifData.get("timeSent");

        return new Notification(sendTo, content, timeSent, isOrganiser, isAdmin);
    }
}
