package com.example.projectv2.Controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.R;
import com.example.projectv2.Controller.ImageController;

import java.util.List;

/**
 * Adapter for displaying a list of event statuses in a RecyclerView.
 */
public class EventStatusAdapter extends RecyclerView.Adapter<EventStatusAdapter.ViewHolder> {

    private static final String TAG = "EventStatusAdapter";
    private final List<String> eventList;
    private final Context context;

    public EventStatusAdapter(List<String> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_event_satus_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String eventName = eventList.get(position);
        holder.eventName.setText(eventName);

        // Fetch and display the event image
        ImageController imageController = new ImageController();
        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Show placeholder while loading
                        .error(R.drawable.placeholder_event) // Show placeholder on error
                        .centerCrop()
                        .into(holder.eventImage);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + eventName, e);
                holder.eventImage.setImageResource(R.drawable.placeholder_event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Updates the event list with a new list of events and notifies the adapter to refresh the view.
     *
     * @param newEvents the new list of events to display
     */
    public void updateEventList(List<String> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public ImageView eventImage;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.event_status_name_text);
            eventImage = view.findViewById(R.id.backgroundImage);
        }
    }
}