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

public class DeadlineWorker extends Worker {
    private final FirebaseFirestore db;

    public DeadlineWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = FirebaseFirestore.getInstance();
    }

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

    private boolean isDeadlinePassed(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date deadlineDate = sdf.parse(deadline);
            return deadlineDate != null && deadlineDate.before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void moveUsersToCancelled(String eventId, List<String> selectedList) {
        db.collection("events").document(eventId)
                .update("entrantList.Cancelled", FieldValue.arrayUnion(selectedList.toArray()))
                .addOnSuccessListener(aVoid -> {
                    db.collection("events").document(eventId)
                            .update("entrantList.Selected", FieldValue.arrayRemove(selectedList.toArray()))
                            .addOnSuccessListener(innerVoid -> {
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeadlineWorker", "Failed to move users to Cancelled list: ", e);

                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DeadlineWorker", "Failed to move users to Cancelled list: ", e);

                });
    }
}

