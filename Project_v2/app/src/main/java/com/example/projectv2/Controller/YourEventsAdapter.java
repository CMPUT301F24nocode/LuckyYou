package com.example.projectv2.Controller;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.R;

import java.util.List;

public class YourEventsAdapter extends RecyclerView.Adapter<YourEventsAdapter.ViewHolder> {

    private final List<String> eventList;

    public YourEventsAdapter(List<String> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_your_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventName.setText(eventList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.your_event_event_name_text);
        }
    }
}
