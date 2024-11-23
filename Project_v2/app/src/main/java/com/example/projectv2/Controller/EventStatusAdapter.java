/**
 * EventStatusAdapter is an adapter for displaying a list of event statuses in a RecyclerView.
 * It binds a list of event names to views within each item of the RecyclerView.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.R;

import java.util.List;

/**
 * Adapter for displaying a list of event statuses in a RecyclerView. Each item in the
 * RecyclerView displays the name of an event.
 */
public class EventStatusAdapter extends RecyclerView.Adapter<EventStatusAdapter.ViewHolder> {

    private final List<String> eventList;

    /**
     * Constructs an EventStatusAdapter with the specified list of event names.
     *
     * @param eventList the list of event names to display
     */
    public EventStatusAdapter(List<String> eventList) {
        this.eventList = eventList;
    }

    /**
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new view will be added
     * @param viewType the view type of the new view
     * @return a new ViewHolder holding the view for each event item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_event_satus_list_object, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the view elements of each item in the RecyclerView.
     *
     * @param holder   the ViewHolder containing view elements to bind data to
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventName.setText(eventList.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the number of events in the event list
     */
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


    /**
     * ViewHolder class to hold and recycle views for each event item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;

        /**
         * Constructs a ViewHolder and initializes the view element for an event item.
         *
         * @param view the view that holds event item elements
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.event_status_name_text);
        }
    }
}
