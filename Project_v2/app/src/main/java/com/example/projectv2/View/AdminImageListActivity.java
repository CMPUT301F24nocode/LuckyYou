package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Controller.ImageAdapter;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AdminImageListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Grid with 3 columns

        eventController = new EventController(this);

        // Fetch events from Firestore and display them in the RecyclerView
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(List<Event> events) {
                imageAdapter = new ImageAdapter(events, eventController);
                recyclerView.setAdapter(imageAdapter);
            }

            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                // No implementation needed
            }

            @Override
            public void onError(Exception e) {
                Log.e("AdminImageListActivity", "Error fetching events", e);
            }
        });
    }
}







