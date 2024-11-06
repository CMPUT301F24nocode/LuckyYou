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
    private List<String> entrantList;

    public EntrantListAdapter(Context context, List<String> entrantList) {
        this.context = context;
        this.entrantList = entrantList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_list_object, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String entrant = entrantList.get(position);
        holder.entrantNameTextView.setText(entrant);

        holder.sendNotificationButton.setOnClickListener(v -> {
            // Add logic to send notification to this entrant
        });

        holder.cancelEntrantButton.setOnClickListener(v -> {
            // Add logic to cancel this entrant
        });
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    public void updateEntrantList(List<String> newEntrants) {
        entrantList.clear();
        entrantList.addAll(newEntrants);
        notifyDataSetChanged();
    }

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        Button sendNotificationButton;
        Button cancelEntrantButton;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.textView11);
            sendNotificationButton = itemView.findViewById(R.id.send_notification_profile_button);
            cancelEntrantButton = itemView.findViewById(R.id.cancel_entrant_profile_button);
        }
    }
}
