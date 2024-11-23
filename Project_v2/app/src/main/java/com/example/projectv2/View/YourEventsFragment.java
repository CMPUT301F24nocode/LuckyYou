package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.YourEventsAdapter;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;

public class YourEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private YourEventsAdapter adapter;
    private EventController eventController;
    private ArrayList<Event> eventList;

    public YourEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_events, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewYourEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Event List
        eventList = new ArrayList<>();

        // Initialize Adapter
        adapter = new YourEventsAdapter(getContext(), eventList);
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
        Log.d("YourEventsFragment", "Fetching user's events...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("YourEventsFragment", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events); // Update adapter with fetched events
            }

            @Override
            public void onEventCreated(String eventId) {
                // Not applicable for this fragment
            }

            @Override
            public void onError(Exception e) {
                Log.e("YourEventsFragment", "Error fetching user's events", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null); // Avoid potential memory leaks
    }
}
