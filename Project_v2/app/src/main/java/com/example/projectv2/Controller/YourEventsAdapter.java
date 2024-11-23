package com.example.projectv2.Controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.example.projectv2.View.EventLandingPageOrganizerActivity;

import java.util.List;

/**
 * Adapter for displaying a list of events that belong to the user in a RecyclerView.
 * Each event item displays the event's name, date, price, and image.
 */
public class YourEventsAdapter extends RecyclerView.Adapter<YourEventsAdapter.ViewHolder> {

    private static final String TAG = "YourEventsAdapter";
    private final List<Event> eventList;
    private final Context context;

    /**
     * Constructs a YourEventsAdapter with the specified context and list of events.
     *
     * @param context   the context in which the adapter is operating
     * @param eventList the list of events to display
     */
    public YourEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    /**
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new view will be added
     * @param viewType the view type of the new view
     * @return a new ViewHolder that holds the view for each event item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_your_events_list_object, parent, false);
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
        Event event = eventList.get(position);

        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");

        // Fetch and display the event image
        ImageController imageController = new ImageController();
        String eventName = event.getName(); // Use the event name to fetch the folder

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                        .error(R.drawable.placeholder_event) // Placeholder if loading fails
                        .centerCrop()
                        .into(holder.eventImage); // Set the retrieved image to the ImageView
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + eventName, e);
                holder.eventImage.setImageResource(R.drawable.placeholder_event); // Set placeholder on failure
            }
        });

        // Set OnClickListener to navigate to EventLandingPageOrganizerActivity with event details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventLandingPageOrganizerActivity.class);
            intent.putExtra("name", event.getName());
            intent.putExtra("details", event.getDetail());
            intent.putExtra("rules", event.getRules());
            intent.putExtra("deadline", event.getDeadline());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("price", event.getTicketPrice());
            intent.putExtra("eventID", event.getEventID());
            intent.putExtra("owner", event.getOwner());
            if (event.getImageUri() != null) {
                intent.putExtra("imageUri", event.getImageUri().toString());
            }
            context.startActivity(intent);
        });
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
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to hold and recycle views for each event item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice;
        public ImageView eventImage;

        /**
         * Constructs a ViewHolder and initializes view elements for an event item.
         *
         * @param view the view that holds event item elements
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.your_event_name_text);
            eventDate = view.findViewById(R.id.your_event_date_text);
            eventPrice = view.findViewById(R.id.your_event_price_text);
            eventImage = view.findViewById(R.id.backgroundImage); // Reference to the ImageView
        }
    }
}
