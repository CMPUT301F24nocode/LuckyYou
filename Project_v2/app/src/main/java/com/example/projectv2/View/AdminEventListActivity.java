package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.AdminEventsAdapter;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

/**
 * AdminFacilityListActivity displays the list of events available for admin users to browse.
 * It initializes the UI layout and sets up the top bar with the title "Browse Events."
 */
public class AdminEventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminEventsAdapter adapter;
    private EventController eventController;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Initializes the UI layout and sets up the top bar with the title "Browse Events."
     * Initializes the RecyclerView and SwipeRefreshLayout.
     * Fetches and displays the list of events.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_list);

        // Setup top bar
        topBarUtils.topBarSetup(this, "Browse Events", View.INVISIBLE);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminEventsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Perform refresh actions, like reloading data
            fetchEvents();

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });

        // Initialize EventController
        eventController = new EventController(this);

        // Fetch and display events
        fetchEvents();
    }

    /**
     * Fetches the list of events from the database and updates the RecyclerView adapter.
     */
    private void fetchEvents() {
        eventController.fetchEvents(new EventController.EventCallback() {

            // Callback methods for handling event list loading
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("AdminFacilityListActivity", "Fetched " + events.size() + " events.");
                adapter.updateEventList(events);
            }

            // Not needed in this activity
            @Override
            public void onEventCreated(String eventId) {
                // Not needed in this activity
            }

            // Not needed in this activity
            @Override
            public void onError(Exception e) {
                Log.e("AdminFacilityListActivity", "Error fetching events", e);
            }
        });
    }
}
