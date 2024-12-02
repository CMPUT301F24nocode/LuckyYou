
package com.example.projectv2.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying a list of events created by the user in a RecyclerView.
 */
public class YourEventsAdapter extends RecyclerView.Adapter<YourEventsAdapter.ViewHolder> {

    private static final String TAG = "YourEventsAdapter";
    private final List<Event> eventList;
    private final Context context;
    private FirebaseFirestore db;

    /**
     * Constructs a YourEventsAdapter with the specified context and list of events.
     *
     * @param context   the context in which the adapter is operating
     * @param eventList the list of events to display
     */
    public YourEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * ViewHolder for holding and recycling event item views.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_your_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the views in the ViewHolder based on the event's position in the list.
     *
     * @param holder   the ViewHolder that should be updated to represent the contents of the item
     * @param position the position of the item within the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        String eventID = event.getEventID();

        // Bind event data to UI elements
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");

        waitingNum(eventID, size -> {
            holder.waitingNum.setText(size + " Waiting");
        });

        // Load event image
        loadEventImage(event, holder.eventImage);

        // Set OnClickListener to navigate to EventLandingPageOrganizerActivity
        holder.itemView.setOnClickListener(v -> navigateToEventDetails(event));
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return the total count of events
     */
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
     * Retrieves the number of users on the waiting list for the specified event.
     *
     * @param eventID  the ID of the event to retrieve the waiting list for
     * @param callback the callback to return the waiting list size
     */
    private void waitingNum(String eventID, WaitingListCallback callback) {
        db.collection("events").document(eventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        // Retrieve the Waiting list
                        List<String> waitingList = (List<String>) document.get("entrantList.Waiting");
                        if (waitingList != null) {
                            callback.onWaitingListSizeReceived(waitingList.size());
                        } else {
                            callback.onWaitingListSizeReceived(0); // No waiting list found
                        }
                    } else {
                        Log.e("waitingNum", "Error fetching document or document does not exist", task.getException());
                        callback.onWaitingListSizeReceived(-1); // Indicate an error
                    }
                });
    }

    /**
     * Callback interface for returning the size of the waiting list.
     */
    public interface WaitingListCallback {
        void onWaitingListSizeReceived(int size);
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
            /**
             * Loads the event image into the ImageView on successful retrieval.
             *
             * @param downloadUrl the download URL of the image
             */
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                if (context instanceof androidx.fragment.app.FragmentActivity) {
                    androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) context;
                    if (!activity.isDestroyed() && !activity.isFinishing()) {
                        Glide.with(context)
                                .load(downloadUrl)
                                .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                                .error(R.drawable.placeholder_event) // Placeholder on error
                                .centerCrop()
                                .into(eventImage);
                    } else {
                        Log.w(TAG, "Activity is destroyed or finishing. Skipping image load.");
                    }
                }
            }

            /**
             * Sets a fallback image on failure to retrieve the event image.
             *
             * @param e the exception that occurred
             */
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
        public TextView eventName, eventDate, eventPrice, waitingNum;
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

            waitingNum = view.findViewById(R.id.your_event_waiting_text);
        }
    }
}
