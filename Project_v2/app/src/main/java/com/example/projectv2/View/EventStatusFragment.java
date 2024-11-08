/**
 * Fragment that displays a list of event statuses in a RecyclerView.
 * The event status list can be updated as needed.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.EventStatusAdapter;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * EventStatusFragment manages and displays a list of event statuses in a vertically scrolling list.
 * The fragment utilizes a RecyclerView and an adapter to display the list of events.
 */
public class EventStatusFragment extends Fragment {

    /**
     * Default constructor for EventStatusFragment.
     */
    public EventStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * Sets up the RecyclerView and its adapter for displaying event statuses.
     *
     * @param inflater           the LayoutInflater used to inflate any views in the fragment
     * @param container          the parent view that this fragment's UI should attach to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return the View for the fragment's UI, or null if it is not associated with a UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_status, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEventStatus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EventStatusAdapter adapter = new EventStatusAdapter(getEventStatusList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Returns a list of sample event statuses to display in the RecyclerView.
     * This can be replaced or modified to fetch actual event statuses from a data source.
     *
     * @return a list of sample event status names
     */
    private List<String> getEventStatusList() {
        List<String> events = new ArrayList<>();
        events.add("Event 1");
        events.add("Event 2");
        events.add("Event 3");
        return events;
    }
}
