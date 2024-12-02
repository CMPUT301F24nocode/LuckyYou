package com.example.projectv2.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.projectv2.MainActivity;
import com.example.projectv2.R;
import com.example.projectv2.View.EventLandingPageUserActivity;
import com.example.projectv2.View.SignUpActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the received message
        if (remoteMessage.getNotification() != null) {

            Log.d("MyFirebaseMessagingService", "remoteMessage: " + remoteMessage);
            Log.d("MyFirebaseMessagingService", "remoteMessage data: " + remoteMessage.getData().get("eventID"));

            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("eventID"));
        }
    }

    private void sendNotification(String messageBody, String eventID) {
        Intent intent = getIntent(eventID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "default_channel_id";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_logo) // Provide your own icon
                        .setContentTitle("Lucky You")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android Oreo and above, you need a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @NonNull
    private Intent getIntent(String eventID) {
        Intent intent;

        // Handle specific eventID cases
        if ("-1".equals(eventID)) {
            // Redirect the user to MainActivity
            intent = new Intent(this, MainActivity.class);
        } else if ("-2".equals(eventID)) {
            // Do not redirect the user to SignUpActivity
            intent = new Intent(this, SignUpActivity.class);
        } else {
            // Default behavior for other event IDs
            intent = new Intent(this, EventLandingPageUserActivity.class);
            intent.putExtra("eventID", eventID);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
