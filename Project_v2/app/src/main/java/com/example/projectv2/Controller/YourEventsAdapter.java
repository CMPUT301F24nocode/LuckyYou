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
 * Adapter for displaying a list of events created by the user in a RecyclerView.
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_your_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Bind event data to UI elements
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");

        // Load event image
        loadEventImage(event, holder.eventImage);

        // Set OnClickListener to navigate to EventLandingPageOrganizerActivity
        holder.itemView.setOnClickListener(v -> navigateToEventDetails(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Updates the event list and refreshes the adapter view.
     *
     * @param newEvents the new list of events to display
     */
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    /**
     * Loads the event image using the ImageController.
     *
     * @param event      the event whose image needs to be loaded
     * @param eventImage the ImageView to display the image
     */
    private void loadEventImage(Event event, ImageView eventImage) {
        ImageController imageController = new ImageController();
        String eventName = event.getName();

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                        .error(R.drawable.placeholder_event) // Placeholder on error
                        .centerCrop()
                        .into(eventImage);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + eventName, e);
                eventImage.setImageResource(R.drawable.placeholder_event); // Set fallback image
            }
        });
    }

    /**
     * Navigates to EventLandingPageOrganizerActivity with the provided event details.
     *
     * @param event the event whose details are to be displayed
     */
    private void navigateToEventDetails(Event event) {
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
    }

    /**
     * ViewHolder for holding and recycling event item views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice;
        public ImageView eventImage;

        /**
         * Constructs a ViewHolder and initializes view elements.
         *
         * @param view the view containing the event item elements
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.your_event_name_text);
            eventDate = view.findViewById(R.id.your_event_date_text);
            eventPrice = view.findViewById(R.id.your_event_price_text);
            eventImage = view.findViewById(R.id.backgroundImage);
        }
    }
}