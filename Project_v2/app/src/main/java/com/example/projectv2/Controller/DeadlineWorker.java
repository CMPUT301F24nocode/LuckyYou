package com.example.projectv2.Controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * DeadlineWorker is a background worker responsible for checking event deadlines
 * and moving users from the "Selected" entrant list to the "Cancelled" entrant list
 * in Firebase Firestore if the deadline has passed.
 */
public class DeadlineWorker extends Worker {
    private final FirebaseFirestore db;

    /**
     * Constructor for DeadlineWorker.
     *
     * @param context      The application context.
     * @param workerParams Parameters for the worker.
     */
    public DeadlineWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Performs the background task of checking event deadlines and updating the entrant lists.
     *
     * @return The result of the worker's operation, which is always {@link Result#success()}.
     */
    @NonNull
    @Override
    public Result doWork() {
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String deadline = document.getString("deadline");
                            if (isDeadlinePassed(deadline)) {
                                List<String> selectedList = (List<String>) document.get("entrantList.Selected");
                                if (selectedList != null && !selectedList.isEmpty()) {
                                    String eventId = document.getId();
                                    moveUsersToCancelled(eventId, selectedList);
                                }
                            }
                        }
                    }
                });

        return Result.success();
    }

    /**
     * Checks if a given deadline has already passed.
     *
     * @param deadline The deadline date as a string in the format "dd-MM-yyyy".
     * @return {@code true} if the deadline has passed; {@code false} otherwise.
     */
    private boolean isDeadlinePassed(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date deadlineDate = sdf.parse(deadline);
            Date currentDate = sdf.parse(sdf.format(new Date()));

            return deadlineDate != null && currentDate.after(deadlineDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Moves users from the "Selected" entrant list to the "Cancelled" entrant list
     * for a specific event in Firestore.
     *
     * @param eventId      The ID of the event document in Firestore.
     * @param selectedList The list of users to be moved.
     */
    private void moveUsersToCancelled(String eventId, List<String> selectedList) {
        db.collection("events").document(eventId)
                .update("entrantList.Cancelled", FieldValue.arrayUnion(selectedList.toArray()))
                .addOnSuccessListener(aVoid -> {
                    db.collection("events").document(eventId)
                            .update("entrantList.Selected", FieldValue.arrayRemove(selectedList.toArray()))
                            .addOnSuccessListener(innerVoid -> {
                                // Successfully moved users to the Cancelled list
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeadlineWorker", "Failed to remove users from Selected list: ", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DeadlineWorker", "Failed to add users to Cancelled list: ", e);
                });
    }
}
