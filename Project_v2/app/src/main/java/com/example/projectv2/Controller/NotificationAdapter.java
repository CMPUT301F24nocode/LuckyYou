package com.example.projectv2.Controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;

import java.util.List;

/**
 * Adapter for managing and displaying notifications in a RecyclerView.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications; // List of notifications to display

    /**
     * Constructs a NotificationAdapter with a list of notifications.
     *
     * @param notifications List of notifications to display in the RecyclerView
     */
    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new NotificationViewHolder holding the View for each notification item
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_object, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     * Binds data to the views in the ViewHolder based on the notification's position in the list.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item
     * @param position The position of the item within the list
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.contentTextView.setText(notification.getContent());
        holder.timeSentTextView.setText(notification.getTimeSent());
    }

    /**
     * Returns the total number of notifications in the list.
     *
     * @return The total count of notifications
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder class for holding and recycling views in the RecyclerView.
     */
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView, timeSentTextView; // TextViews for notification content and time

        /**
         * Constructs a NotificationViewHolder and initializes the views.
         *
         * @param itemView The view of the individual notification item
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.notification_content_view);
            timeSentTextView = itemView.findViewById(R.id.notification_content_time_view);
        }
    }
}
