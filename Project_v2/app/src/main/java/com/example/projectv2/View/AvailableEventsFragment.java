/**
 * Fragment for displaying a list of available events. The fragment fetches events from
 * Firebase and displays them in a RecyclerView. It also allows users to create new events
 * via an activity and updates the display upon event creation.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

/**
 * AvailableEventsFragment displays a list of available events for users to browse.
 * It initializes a RecyclerView with an adapter to display events fetched from Firebase.
 * Users can also create new events, which are added to Firebase and displayed in the list.
 */
public class AvailableEventsFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private EventController eventController;
    private AvailableEventsAdapter adapter;

    /**
     * Default constructor. Required for instantiating the fragment.
     */
    public AvailableEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with the fragment. Sets up the
     * RecyclerView for displaying events and initializes the EventController for
     * Firestore operations.
     *
     * @param inflater           the LayoutInflater object to inflate the view
     * @param container          the parent view that this fragment is attached to
     * @param savedInstanceState saved state information
     * @return the root view of the fragment
     */
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

        // Initialize EventController for Firestore operations
        eventController = new EventController(getActivity());

        // Fetch events from Firebase and update display
        fetchEventsFromFirebase();

        return view;
    }

    /**
     * Fetches events from Firebase using the EventController and updates the adapter
     * with the retrieved list of events.
     */
    private void fetchEventsFromFirebase() {
        Log.d("AvailableEventsFragment", "Starting Firebase fetch...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("AvailableEventsFragment", "Fetched " + events.size() + " events from Firebase.");
                adapter.updateEventList(events);
            }

            @Override
            public void onEventCreated(String eventId) {
                Log.d("AvailableEventsFragment", "Event created with ID: " + eventId);
            }

            @Override
            public void onError(Exception e) {
                Log.e("AvailableEventsFragment", "Error fetching events", e);
            }
        });
    }
}
