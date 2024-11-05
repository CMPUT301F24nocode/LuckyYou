package com.example.projectv2.Controller;

// services/NotificationService.java
import com.example.projectv2.Model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing notifications and storing them in Firebase Firestore.
 */
public class NotificationService {

    private final FirebaseFirestore db; // Firebase Firestore instance for database operations
    private String eventName;

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
    public void sendNotification(Notification notification) {
        String userId = notification.getSendTo(); // Get document ID from sendTo attribute
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("content", notification.getContent());
        notificationData.put("timeSent", notification.getTimeSent());

        // If the notification is for organisers, add to organiser_notif array
        if (notification.isOrganiser()) {
            db.collection("Users").document(userId)
                    .update("organiser_notif", FieldValue.arrayUnion(notificationData))
                    .addOnSuccessListener(aVoid -> {
                        // Log success if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
        //waiting list
        NotificationService notificationService = new NotificationService();
        notificationService.sendNotification(new Notification(
                userId,
                "You have successfully joined the waiting list for the event " + eventName,
                false, // isOrganiser
                false  // isAdmin
        ));

        // If the notification is for admins, add to admin_notif array
        if (notification.isAdmin()) {
            db.collection("Users").document(userId)
                    .update("admin_notif", FieldValue.arrayUnion(notificationData))
                    .addOnSuccessListener(aVoid -> {
                        // Log success if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }
}
