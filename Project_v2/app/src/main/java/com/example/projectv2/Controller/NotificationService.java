package com.example.projectv2.Controller;

// services/NotificationService.java
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectv2.ApiClient;
import com.example.projectv2.Model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Service class for managing notifications and storing them in Firebase Firestore.
 */
public class NotificationService {

    private final FirebaseFirestore db; // Firebase Firestore instance for database operations

    /**
     * Constructs a NotificationService and initializes the Firestore database instance.
     */
    public NotificationService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Sends a notification by updating the Firestore database.
     * Stores notification data in either organiser or admin arrays based on flags in the Notification object.
     *
     * @param notification The notification to be sent and stored in Firestore
     */
    public void sendNotification(Activity activity, Notification notification) {
        String userId = notification.getSendTo(); // Get document ID from sendTo attribute
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("content", notification.getContent());
        notificationData.put("timeSent", notification.getTimeSent());

        // If the notification is for organisers, add to organiser_notif array
        if (notification.isOrganiser()) {
            db.collection("Users").document(userId)
                    .update("organizerNotifList", FieldValue.arrayUnion(notificationData))
                    .addOnSuccessListener(aVoid -> {
                        sendPushNotification(activity, userId, notification.getContent());
                        Log.d("sendNotificationResult", "Notification sent to FireStore");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("sendNotificationResult", "Notification not sent to FireStore" + e);
                    });
        }

        // If the notification is for admins, add to adminNotifList array
        if (notification.isAdmin()) {
            db.collection("Users").document(userId)
                    .update("adminNotifList", FieldValue.arrayUnion(notificationData))
                    .addOnSuccessListener(aVoid -> {
                        sendPushNotification(activity, userId, notification.getContent());
                        Log.d("sendNotificationResult", "Notification sent to FireStore");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("sendNotificationResult", "Notification not sent to FireStore" + e);
                    });
        }
    }

    private void sendPushNotification (Activity activity, String userId, String pushNotifContent) {
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String fcmToken = documentSnapshot.getString("fcmToken");

                        Log.d("fcmTokenResult", "FCM Token: " + fcmToken);

                        PushNotificationService service = ApiClient.getClient().create(PushNotificationService.class);

                        NotificationRequest request = new NotificationRequest(
                                fcmToken,
                                pushNotifContent
                        );

                        service.sendNotification(request).enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    Toast.makeText(activity, "Notifications sent!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Log.d("fcmTokenResult", "Error? " + t);
                                    Toast.makeText(activity, "Failed to send notifications!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Log.d("fcmTokenResult", "Document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("fcmTokenResult", "Error retrieving document", e);
                });
    }

    interface PushNotificationService {
        @POST("sendNotification")
        Call<Void> sendNotification(@Body NotificationRequest request);
    }

    static class NotificationRequest {
        String token;
        String body;

        NotificationRequest(String token, String body) {
            this.token = token;
            this.body = body;
        }
    }
}
