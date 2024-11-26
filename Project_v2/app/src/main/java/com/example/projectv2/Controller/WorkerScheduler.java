package com.example.projectv2.Controller;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkerScheduler {

    public static void scheduleDeadlineCheck(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DeadlineWorker.class,
                1, TimeUnit.HOURS // Run every hour
        ).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DeadlineWorker",
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }
}
