/**
 * Adapter for displaying a list of entrants in a RecyclerView. Each entrant item includes
 * options to send a notification or cancel the entrant. The adapter supports updating the list of entrants.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.R;
import com.example.projectv2.View.SendNotificationOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * EntrantListAdapter manages the display of a list of entrants in a RecyclerView.
 * Each item in the list includes an entrant name or ID with buttons to send a notification
 * or cancel the entrant's participation.
 */
public class EntrantListAdapter extends RecyclerView.Adapter<EntrantListAdapter.EntrantViewHolder> {

    private final Context context;
    private final List<String> entrantList;
    private final List<String> documentIds;
    private int currentlyVisiblePosition = RecyclerView.NO_POSITION;

    /**
     * Constructs an EntrantListAdapter with the specified context and entrant list.
     *
     * @param context     the context in which the adapter is operating
     * @param entrantList the list of entrant names or IDs to display
     */
    public EntrantListAdapter(Context context, List<String> entrantList, List<String> documentIds) {
        this.context = context;
        this.entrantList = entrantList;
        this.documentIds = documentIds;
    }

    /**
     * Inflates the layout for each entrant item in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new view will be added
     * @param viewType the view type of the new view
     * @return a new EntrantViewHolder that holds the view for each entrant item
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_list_object, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds data to the view elements of each item in the RecyclerView.
     *
     * @param holder   the EntrantViewHolder containing view elements to bind data to
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String entrant = entrantList.get(position);
        String documentId = documentIds.get(position); // Get the document ID for this entrant
        holder.entrantNameTextView.setText(entrant);

        // Show or hide buttons based on the currently selected position
        if (position == currentlyVisiblePosition) {
            holder.sendNotificationButton.setVisibility(View.VISIBLE);
            holder.cancelEntrantButton.setVisibility(View.VISIBLE);
        } else {
            holder.sendNotificationButton.setVisibility(View.GONE);
            holder.cancelEntrantButton.setVisibility(View.GONE);
        }

        // Handle item click to show buttons for the selected item
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = currentlyVisiblePosition;
            currentlyVisiblePosition = position;

            // Notify the adapter about the changes to update the visibility of the buttons
            notifyItemChanged(previousPosition);
            notifyItemChanged(currentlyVisiblePosition);

            Log.d("EntrantListAdapter", "Item clicked: " + entrant + ", Document ID: " + documentId);
        });

        // Handle the Send Notification button click for this entrant
        holder.sendNotificationButton.setOnClickListener(v -> {
            Log.d("EntrantListAdapter", "Send notification clicked for: " + entrant + ", Document ID: " + documentId);

            // Pass the documentId as part of a list to SendNotificationOverlay
            ArrayList<String> documentIdsForOverlay = new ArrayList<>();
            documentIdsForOverlay.add(documentId); // Only the selected entrant's document ID

            // Retrieve the event ID from the activity's context
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                String eventId = activity.getIntent().getStringExtra("eventId");

                // Create and show the overlay fragment
                SendNotificationOverlay overlay = SendNotificationOverlay.newInstance(activity, documentIdsForOverlay, eventId);
                if (activity instanceof androidx.fragment.app.FragmentActivity) {
                    overlay.show(((androidx.fragment.app.FragmentActivity) activity).getSupportFragmentManager(), "SendNotificationOverlay");
                }
            } else {
                Log.e("EntrantListAdapter", "Context is not an instance of Activity");
            }
        });

        // Handle the Cancel Entrant button click for this entrant
        holder.cancelEntrantButton.setOnClickListener(v -> {
            Log.d("EntrantListAdapter", "Cancel entrant clicked for: " + entrant + ", Document ID: " + documentId);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                String eventId = activity.getIntent().getStringExtra("eventId");

                DBUtils.removeUser(documentId, eventId);
            }
        });
    }

    /**
     * Returns the total number of entrants in the list.
     *
     * @return the total number of entrants
     */
    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    /**
     * Updates the list of entrants in the adapter and notifies the RecyclerView to refresh the data.
     *
     * @param newEntrants the new list of entrants to display
     */
    public void updateEntrantList(List<String> newEntrants, List<String> newDocumentIds) {
        entrantList.clear();
        documentIds.clear();
        entrantList.addAll(newEntrants);
        documentIds.addAll(newDocumentIds);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for each entrant item in the RecyclerView. Holds references to the UI elements
     * within each entrant item.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        Button sendNotificationButton;
        Button cancelEntrantButton;

        /**
         * Constructs an EntrantViewHolder and initializes UI elements for each entrant item.
         *
         * @param itemView the view that holds entrant item elements
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.textView11);
            sendNotificationButton = itemView.findViewById(R.id.send_notification_profile_button);
            cancelEntrantButton = itemView.findViewById(R.id.cancel_entrant_profile_button);
        }
    }
}
