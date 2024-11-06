package com.example.projectv2.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.R;

import java.util.List;

public class EntrantListAdapter extends RecyclerView.Adapter<EntrantListAdapter.EntrantViewHolder> {

    private Context context;
    private List<String> entrantList; // List of entrant names or IDs

    // Constructor to initialize context and entrant list
    public EntrantListAdapter(Context context, List<String> entrantList) {
        this.context = context;
        this.entrantList = entrantList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each entrant item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_list_object, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        // Get the entrant name or ID from the list
        String entrant = entrantList.get(position);
        holder.entrantNameTextView.setText(entrant); // Display entrant name or ID in TextView

        // Handle the Send Notification button click for this entrant
        holder.sendNotificationButton.setOnClickListener(v -> {
            // Add logic to send notification to this entrant
        });

        // Handle the Cancel Entrant button click for this entrant
        holder.cancelEntrantButton.setOnClickListener(v -> {
            // Add logic to cancel this entrant
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of entrants in the list
        return entrantList.size();
    }

    // Method to update the list of entrants in the adapter
    public void updateEntrantList(List<String> newEntrants) {
        entrantList.clear(); // Clear existing list
        entrantList.addAll(newEntrants); // Add new list of entrants
        notifyDataSetChanged(); // Notify RecyclerView to refresh data
    }

    // ViewHolder class for each entrant item
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        Button sendNotificationButton;
        Button cancelEntrantButton;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI elements for each entrant item
            entrantNameTextView = itemView.findViewById(R.id.textView11);
            sendNotificationButton = itemView.findViewById(R.id.send_notification_profile_button);
            cancelEntrantButton = itemView.findViewById(R.id.cancel_entrant_profile_button);
        }
    }
}
