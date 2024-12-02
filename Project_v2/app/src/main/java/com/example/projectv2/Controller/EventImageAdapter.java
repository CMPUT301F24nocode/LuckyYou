package com.example.projectv2.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.R;

import java.util.List;

public class EventImageAdapter extends RecyclerView.Adapter<EventImageAdapter.ImageViewHolder> {

    private static final String TAG = "EventImageAdapter";
    private final Context context;
    private final List<String> imageFilenames; // List of filenames
    private final ImageController imageController;

    public EventImageAdapter(Context context, List<String> imageFilenames) {
        this.context = context;
        this.imageFilenames = imageFilenames; // Assume placeholders are already filtered
        this.imageController = new ImageController(); // Initialize ImageController
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_image_list_object, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String filename = imageFilenames.get(position); // Get the filename

        // Fetch and display the image
        imageController.getDownloadUrl(filename, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .into(holder.imageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to fetch image: " + filename, e);
                holder.imageView.setImageResource(R.drawable.placeholder_event); // Set placeholder
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Attempting to delete image: " + filename);

            // Delete the image
            imageController.deleteImage(filename, new ImageController.ImageDeleteCallback() {
                @Override
                public void onDeleteSuccess() {
                    Log.d(TAG, "Image deleted: " + filename);

                    // Update the UI
                    imageFilenames.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageFilenames.size());

                    Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteFailure(Exception e) {
                    Log.e(TAG, "Failed to delete image: " + filename, e);
                    Toast.makeText(context, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return imageFilenames.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
