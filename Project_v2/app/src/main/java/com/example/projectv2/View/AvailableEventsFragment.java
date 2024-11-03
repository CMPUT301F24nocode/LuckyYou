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

import com.example.projectv2.Controller.AvailableEventsAdapter;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AvailableEventsFragment extends Fragment {

    public AvailableEventsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_events, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAvailableEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AvailableEventsAdapter adapter = new AvailableEventsAdapter(getAvailableEventsList());
        recyclerView.setAdapter(adapter);


        return view;
    }

    private List<String> getAvailableEventsList() {
        List<String> events = new ArrayList<>();
        events.add("Event 1");
        events.add("Event 2");
        events.add("Event 3");
        events.add("Event 4");
        events.add("Event 5");
        return events;
    }
}
