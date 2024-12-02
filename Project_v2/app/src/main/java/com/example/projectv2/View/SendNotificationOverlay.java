package com.example.projectv2.View;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;

import java.util.ArrayList;

public class SendNotificationOverlay extends DialogFragment {

    private ArrayList<String> documentIds;
    private String eventId;
    private Activity parentActivity;

    public static SendNotificationOverlay newInstance(Activity activity, ArrayList<String> documentIds, String eventId) {
        SendNotificationOverlay fragment = new SendNotificationOverlay();
        fragment.parentActivity = activity;
        Bundle args = new Bundle();
        args.putStringArrayList("documentIds", documentIds);
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_notification_overlay, container, false);

        if (getArguments() != null) {
            documentIds = getArguments().getStringArrayList("documentIds");
            eventId = getArguments().getString("eventId");
        }

        Button sendNotificationButton = view.findViewById(R.id.send_notification_overlay_button);
        sendNotificationButton.setOnClickListener(v -> {
            if (parentActivity != null) {

                TextView sendContent = view.findViewById(R.id.send_notification_overlay_content);
                String content = sendContent.getText().toString();

                NotificationService notificationService = new NotificationService();

                for (String userId : documentIds) {
                    Notification notification = new Notification(userId, content, true, false);
                    notificationService.sendNotification(parentActivity, notification, eventId);
                }

                Toast.makeText(parentActivity, "Sending notifications for Event ID: " + eventId + " to: " + documentIds, Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        return view;
    }
}
