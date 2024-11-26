package com.example.projectv2.Controller;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeadlineWorker extends Worker {

    private static final String TAG = "DeadlineWorker";
    private final EventController eventController;

    public DeadlineWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        eventController = new EventController(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Running deadline check");
        eventController.handleExpiredDeadlines(); // Implement this method in EventController
        return Result.success();
    }
}
