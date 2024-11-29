package com.example.projectv2.View;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.BrowseEventsAdapter;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseEventsActivity extends AppCompatActivity {

    private static final String TAG = "BrowseEventsActivity";
    private RecyclerView recyclerView;
    private BrowseEventsAdapter adminEventsAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_list);
        topBarUtils.topBarSetup(this, "Browse Events", View.INVISIBLE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adminEventsAdapter = new BrowseEventsAdapter(this, eventList);
        recyclerView.setAdapter(adminEventsAdapter);

        db = FirebaseFirestore.getInstance();

        // Fetch events from Firestore
        fetchEventsFromFirestore(new EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                adminEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading events: ", e);
                Toast.makeText(BrowseEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());


    }

    public void fetchEventsFromFirestore(EventCallback callback) {
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String owner = document.getString("owner");
                            String name = document.getString("name");
                            String detail = document.getString("detail");
                            String rules = document.getString("rules");
                            String facility = document.getString("facility");
                            String deadline = document.getString("deadline");
                            String startDate = document.getString("startDate");
                            String ticketPrice = document.getString("ticketPrice");
                            String eventID = document.getString("eventID");
                            Uri imageUri = document.getString("imageUri") != null ? Uri.parse(document.getString("imageUri")) : null;

                            Event event = new Event(eventID, owner, name, detail, rules, deadline, startDate, ticketPrice, imageUri, facility);
                            events.add(event);
                        }
                        callback.onEventListLoaded(events);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    public interface EventCallback {
        void onEventListLoaded(ArrayList<Event> events);
        void onError(Exception e);
    }
}
