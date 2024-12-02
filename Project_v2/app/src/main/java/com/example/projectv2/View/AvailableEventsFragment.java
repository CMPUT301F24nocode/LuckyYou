package com.example.projectv2.View;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.AvailableEventsAdapter;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;

public class AvailableEventsFragment extends Fragment {

    private EventController eventController;
    private AvailableEventsAdapter adapter;

    public AvailableEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_events, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAvailableEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableEventsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize EventController
        eventController = new EventController(getActivity());

        // Fetch all events
        refreshEventsFromFirestore();

        return view;
    }

    public void refreshEventsFromFirestore() {
        Log.d("AvailableEventsFragment", "Refreshing available events...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("AvailableEventsFragment", "Fetched " + events.size() + " events.");
                adapter.updateEventList(events);
            }

            @Override
            public void onEventCreated(String eventId) {
                // Not used for refreshing
            }

            @Override
            public void onError(Exception e) {
                Log.e("AvailableEventsFragment", "Error refreshing events", e);
            }
        });
    }
}