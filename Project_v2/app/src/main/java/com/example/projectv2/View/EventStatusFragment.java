package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.EventStatusAdapter;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventStatusFragment extends Fragment {

    private EventStatusAdapter adapter;
    private FirebaseFirestore db;
    private ProgressBar loadingIndicator;
    private TextView emptyStateView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_event_status, container, false);

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEventStatus);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        // Set up RecyclerView with the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventStatusAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch event statuses from Firestore
        fetchEventStatuses();
        return view;
    }

    private void fetchEventStatuses() {
        loadingIndicator.setVisibility(View.VISIBLE);

        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    loadingIndicator.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        List<String> eventNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("name");
                            if (eventName != null) {
                                eventNames.add(eventName);
                            }
                        }

                        if (eventNames.isEmpty()) {
                            emptyStateView.setVisibility(View.VISIBLE);
                        } else {
                            emptyStateView.setVisibility(View.GONE);
                            adapter.updateEventList(eventNames); // Update the adapter
                        }
                    } else {
                        Log.e("EventStatusFragment", "Error fetching events", task.getException());
                    }
                });
    }
}