package com.example.projectv2.Controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Model.User;
import com.example.projectv2.R;

import java.util.List;

/**
 * Adapter class for displaying a list of user profiles in a RecyclerView for administrative purposes.
 * Each user is represented as an item in the RecyclerView.
 */
public class AdminProfileAdapter extends RecyclerView.Adapter<AdminProfileAdapter.ViewHolder> {

    private final List<User> userList;
    private final OnItemClickListener onItemClickListener;

    /**
     * Interface definition for a callback to be invoked when a user item is clicked.
     */
    public interface OnItemClickListener {
        /**
         * Called when a user item is clicked.
         *
         * @param userID the ID of the user that was clicked
         */
        void onItemClick(String userID);
    }

    /**
     * Constructor for initializing the AdminProfileAdapter with a list of users and a click listener.
     *
     * @param userList          the list of users to display
     * @param onItemClickListener a callback to handle click events on user items
     */
    public AdminProfileAdapter(List<User> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Inflates the layout for each RecyclerView item and creates a ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new ViewHolder instance for the inflated item layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_profile_list_object, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data of a user to the corresponding views in the ViewHolder.
     *
     * @param holder   the ViewHolder for the current item
     * @param position the position of the item in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.adminProfileName.setText(user.getName());

        // Set the click listener for user items
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(user.getDeviceID());
            }
        });
    }

    /**
     * Returns the total number of users in the data list.
     *
     * @return the size of the user list
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class that holds the views for displaying a user's profile in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView adminProfileName;

        /**
         * Constructor for initializing the ViewHolder with the corresponding views.
         *
         * @param itemView the item view for the ViewHolder
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adminProfileName = itemView.findViewById(R.id.admin_profile_name);
        }
    }
}
