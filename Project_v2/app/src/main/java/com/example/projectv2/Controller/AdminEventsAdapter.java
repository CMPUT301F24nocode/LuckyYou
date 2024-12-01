package com.example.projectv2.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {

    private static final String TAG = "AdminEventsAdapter";
    private final List<Event> eventList;
    private final Context context;
    private final String userRole;
    private final FirebaseFirestore db;

    public AdminEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        this.userRole = preferences.getString("userRole", "user"); // Default to "user" if role not found
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_available_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");
        holder.eventDescription.setText(event.getDetail());

        // Fetch and display the event image
        ImageController imageController = new ImageController();
        imageController.retrieveImage(event.getName(), new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                        .error(R.drawable.placeholder_event) // Placeholder if loading fails
                        .centerCrop()
                        .into(holder.backgroundImage); // Set the image in the ImageView
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + event.getName(), e);
                holder.backgroundImage.setImageResource(R.drawable.placeholder_event); // Fallback to placeholder
            }
        });

        holder.itemView.setOnClickListener(v -> showOptionsDialog(event));
    }

    private void showOptionsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Options");
        builder.setItems(new CharSequence[]{"Delete Event", "Delete QR Code"}, (dialog, which) -> {
            if (which == 0) {
                deleteEvent(event);
            } else if (which == 1) {
                deleteQRCode(event);
            }
        });
        builder.show();
    }

    private void deleteEvent(Event event) {
        db.collection("events").document(event.getEventID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    eventList.remove(event);
                    notifyDataSetChanged();
                    Log.d(TAG, "Event deleted: " + event.getEventID());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event", e));
    }

    private void deleteQRCode(Event event) {
        db.collection("events").document(event.getEventID())
                .update("qrHashData", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "QR Code deleted for event: " + event.getEventID());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting QR Code", e));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice, eventDescription;
        public ImageView backgroundImage;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.available_event_name_text);
            eventDate = view.findViewById(R.id.available_event_date_text);
            eventPrice = view.findViewById(R.id.available_event_price_text);
            eventDescription = view.findViewById(R.id.available_event_description_text);
            backgroundImage = view.findViewById(R.id.backgroundImage);
        }
    }
}
