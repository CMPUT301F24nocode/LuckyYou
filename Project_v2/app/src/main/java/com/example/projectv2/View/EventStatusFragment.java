package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Controller.EventStatusAdapter;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;

public class EventStatusFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventStatusAdapter adapter;
    private EventController eventController;
    private ArrayList<Event> eventList;

    public EventStatusFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_status, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEventStatus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Event List
        eventList = new ArrayList<>();

        // Initialize Adapter
        adapter = new EventStatusAdapter(getContext(), eventList);
        recyclerView.setAdapter(adapter);

        // Initialize EventController
        eventController = new EventController(getActivity());

        // Fetch Events
        fetchEvents();

        return view;
    }

    /**
     * Fetches events created by the user from Firebase and updates the adapter.
     */
    private void fetchEvents() {
        Log.d("EventStatusFragment", "Fetching user's events...");
        eventController.fetchRelatedEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("EventStatusFragment", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events); // Update adapter with fetched events
            }

            @Override
            public void onEventCreated(String eventId) {
                // Not applicable for this fragment
            }

            @Override
            public void onError(Exception e) {
                Log.e("EventStatusFragment", "Error fetching user's events", e);
            }
        });
    }
}