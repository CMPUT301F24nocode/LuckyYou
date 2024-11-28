package com.example.projectv2.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventEditOverlay extends DialogFragment {

    private FirebaseFirestore db;
    private int attendeesLimit;
    private String eventID, eventName;
    private Activity parentActivity;

    public static EventEditOverlay newInstance(Activity activity, String eventId, String eventName) {
        EventEditOverlay fragment = new EventEditOverlay();
        fragment.parentActivity = activity; // Set the parent activity
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        args.putString("eventName", eventName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_edit_overlay, container, false);

        if (getArguments() != null) {
            eventID = getArguments().getString("eventId");
            eventName = getArguments().getString("eventName");
        }

        Log.d("EventEditDialogFragment", "Dialog created");

        Button chooseAttendeesButton = view.findViewById(R.id.choose_attendees_button);
        chooseAttendeesButton.setOnClickListener(v -> setSelectedList(eventID, eventName));

        return view;
    }

    private void setSelectedList(String eventID, String eventName) {
        Log.d("EventEditDialogFragment", "Button clicked");

        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    attendeesLimit = Integer.parseInt(document.getString("attendees"));
                    List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                    List<String> attendeeList = (List<String>) document.get("entrantList.Attendee");
                    List<String> selectedAttendees;

                    Log.d("EventEditDialogFragment", "Attendee limit: " + attendeesLimit);
                    Log.d("EventEditDialogFragment", "Waiting List: " + waitingList);

                    if (waitingList != null && !waitingList.isEmpty()) {
                        Collections.shuffle(waitingList);

                        Log.d("EventEditDialogFragment", "Waiting list size: " + waitingList.size());

                        if (attendeeList != null) {
                            Log.d("EventEditDialogFragment", "Attendee list size: " + attendeeList.size());
                            if (attendeeList.size() < attendeesLimit) {
                                if (waitingList.size() >= attendeesLimit) {
                                    selectedAttendees = waitingList.subList(0, attendeesLimit);
                                } else {
                                    selectedAttendees = new ArrayList<>(waitingList);
                                }
                            } else {
                                selectedAttendees = Collections.emptyList();
                                Toast.makeText(requireContext(), "Attendee limit reached!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            selectedAttendees = Collections.emptyList();
                        }

                        NotificationService notificationService = new NotificationService();

                        for (String id : selectedAttendees) {
                            Notification notification = new Notification(id, "Congratulations! You have been chosen to attend " + eventName, true, false);
                            notificationService.sendNotification(parentActivity, notification, eventID);
                        }

                        for (String id: waitingList) {
                            if (!selectedAttendees.contains(id)) {
                                Notification notification = new Notification(id, "You were unfortunately not selected for " + eventName + ", Don't worry. You may get another chance. Keep alert!", true, false);
                                notificationService.sendNotification(parentActivity, notification, eventID);
                            }
                        }

                        eventRef.update("entrantList.Selected", FieldValue.arrayUnion(selectedAttendees.toArray()))
                                .addOnSuccessListener(aVoid -> {
                                    for (String attendee : selectedAttendees) {
                                        eventRef.update("entrantList.Waiting", FieldValue.arrayRemove(attendee))
                                                .addOnSuccessListener(innerVoid -> Log.d("EventEditDialogFragment", "Attendee moved successfully: " + attendee))
                                                .addOnFailureListener(e -> Log.e("EventEditDialogFragment", "Error removing attendee: ", e));
                                    }
                                    Toast.makeText(requireContext(), "Attendees chosen successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to update selected attendees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("EventEditDialogFragment", "Error updating selected list: ", e);
                                });
                    } else {
                        Toast.makeText(requireContext(), "No users in the waiting list to select.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Event not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}