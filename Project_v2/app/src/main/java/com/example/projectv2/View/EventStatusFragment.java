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

public class EventStatusFragment extends Fragment {

    public EventStatusFragment() {
        // Required empty public constructor
    }

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

    private List<String> getEventStatusList() {
        List<String> events = new ArrayList<>();
        events.add("Event 1");
        events.add("Event 2");
        events.add("Event 3");
        return events;
    }
}
