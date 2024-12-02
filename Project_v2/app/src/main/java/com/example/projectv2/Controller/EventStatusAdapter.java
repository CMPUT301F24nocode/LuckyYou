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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.example.projectv2.View.EventLandingPageUserActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying a list of event statuses in a RecyclerView.
 */
public class EventStatusAdapter extends RecyclerView.Adapter<EventStatusAdapter.ViewHolder> {

    private static final String TAG = "EventStatusAdapter";
    private final List<Event> eventList;
    private final Context context;
    private final FirebaseFirestore db;

    public EventStatusAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        db = FirebaseFirestore.getInstance();
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
        Event event = eventList.get(position);
        String eventID = event.getEventID();

        setTextStatus(eventID, status -> {
            if (status == 0) {
                holder.waiting.setVisibility(View.VISIBLE);
            } else if (status == 1) {
                holder.accepted.setVisibility(View.VISIBLE);
            } else if (status == 2) {
                holder.declined.setVisibility(View.VISIBLE);
            }
        });

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
     * Updates the event list with a new list of events and notifies the adapter to refresh the view.
     *
     * @param newEvents the new list of events to display
     */
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public interface StatusCallback {
        void onStatusFetched(int status);
    }

    private void setTextStatus(String eventID, StatusCallback callback) {
        @SuppressLint("HardwareIds")
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").document(eventID).get()
                .addOnCompleteListener(task -> {
                    int status = 0; // Default status
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, List<String>> entrantList = (Map<String, List<String>>) document.get("entrantList");

                        if (entrantList != null) {
                            if (entrantList.get("Waiting") != null && entrantList.get("Waiting").contains(deviceID)) {
                                status = 0; // Waiting
                            } else if ((entrantList.get("Selected") != null && entrantList.get("Selected").contains(deviceID)) ||
                                    (entrantList.get("Attendee") != null && entrantList.get("Attendee").contains(deviceID))) {
                                status = 1; // Selected or Attendee
                            } else if (entrantList.get("Cancelled") != null && entrantList.get("Cancelled").contains(deviceID)) {
                                status = 2; // Cancelled
                            }
                        }
                    } else {
                        Log.e("setTextStatus", "Error fetching document or document does not exist", task.getException());
                    }

                    // Pass the status back through the callback
                    callback.onStatusFetched(status);
                });
    }

    private void loadEventImage(Event event, ImageView eventImage) {
        ImageController imageController = new ImageController();
        String eventName = event.getName();

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
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

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + eventName, e);
                eventImage.setImageResource(R.drawable.placeholder_event); // Set fallback image
            }
        });
    }



    /**
     * Navigates to EventLandingPageUserActivity with the provided event details.
     *
     * @param event the event whose details are to be displayed
     */
    private void navigateToEventDetails(Event event) {
        Intent intent = new Intent(context, EventLandingPageUserActivity.class);
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
        public TextView eventName, eventDate, eventPrice, declined, accepted, waiting;
        public ImageView eventImage;

        /**
         * Constructs a ViewHolder and initializes view elements.
         *
         * @param view the view containing the event item elements
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.event_status_name_text);
            eventDate = view.findViewById(R.id.event_status_date_text);
            eventPrice = view.findViewById(R.id.event_status_price_text);
            eventImage = view.findViewById(R.id.backgroundImage);

            declined = view.findViewById(R.id.event_status_declined_text);
            accepted = view.findViewById(R.id.event_status_confirmed_text);
            waiting = view.findViewById(R.id.event_status_waiting_text);

        }
    }
}
