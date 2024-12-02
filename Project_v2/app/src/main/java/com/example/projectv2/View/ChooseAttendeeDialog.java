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

/**
 * Dialog fragment for choosing attendees for an event.
 *
 * <p>This dialog allows event organizers to select attendees from a waiting list,
 * notify them, and update Firestore with the selected and remaining waiting lists.</p>
 */
public class ChooseAttendeeDialog extends DialogFragment {

    private FirebaseFirestore db;
    private int attendeesLimit;
    private String eventID, eventName;
    private Activity parentActivity;

    /**
     * Creates a new instance of ChooseAttendeeDialog.
     *
     * @param activity The parent activity from which this dialog is launched.
     * @param eventId  The ID of the event.
     * @param eventName The name of the event.
     * @return A new instance of {@link ChooseAttendeeDialog}.
     */
    public static ChooseAttendeeDialog newInstance(Activity activity, String eventId, String eventName) {
        ChooseAttendeeDialog fragment = new ChooseAttendeeDialog();
        fragment.parentActivity = activity;
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        args.putString("eventName", eventName);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the dialog is attached to the context.
     * Initializes Firebase Firestore.
     *
     * @param context The context to which the dialog is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Inflates the layout for the dialog and sets up button click listeners.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container The parent view that this fragment's UI should be attached to, if applicable.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
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

    /**
     * Handles the selection of attendees from the waiting list and updates the Firestore database.
     *
     * @param eventID The ID of the event.
     * @param eventName The name of the event.
     */
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

                        // Notify selected attendees
                        for (String id : selectedAttendees) {
                            Notification notification = new Notification(id, "Congratulations! You have been chosen to attend " + eventName, true, false);
                            notificationService.sendNotification(parentActivity, notification, eventID);
                        }

                        // Notify non-selected attendees
                        for (String id: waitingList) {
                            if (!selectedAttendees.contains(id)) {
                                Notification notification = new Notification(id, "You were unfortunately not selected for " + eventName + ", Don't worry. You may get another chance. Keep alert!", true, false);
                                notificationService.sendNotification(parentActivity, notification, eventID);
                            }
                        }

                        // Update Firestore with selected attendees
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
