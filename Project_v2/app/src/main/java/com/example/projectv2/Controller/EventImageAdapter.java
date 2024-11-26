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
    private final List<String> imageFilenames; // Use relative filenames directly
    private final ImageController imageController;
    public EventImageAdapter(Context context, List<String> imageFilenames) {
        // Filter out placeholder_event.png during initialization
        this.context = context;
        this.imageFilenames = filterPlaceholders(imageFilenames);
        this.imageController = new ImageController(); // Initialize ImageController
    }

    private List<String> filterPlaceholders(List<String> filenames) {
        filenames.removeIf(filename -> filename.contains("placeholder_event.png"));
        return filenames;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_image_list_object, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String filename = imageFilenames.get(position); // Get the filename directly

        // Construct the download URL for displaying the image
        imageController.getDownloadUrl(filename, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                if (downloadUrl.contains("placeholder_event.png")) {
                    Log.d(TAG, "Skipping placeholder image: " + filename);
                    return; // Do not load placeholder images
                }

                // Load the image using Glide
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .into(holder.imageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                // Handle errors (e.g., set placeholder if the image URL cannot be retrieved)
                Log.e(TAG, "Failed to fetch download URL for: " + filename, e);
                holder.imageView.setImageResource(R.drawable.placeholder_event);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            String placeholderPath = "placeholder_event.png"; // Path to placeholder image

            Log.d(TAG, "Deleting image: " + filename);

            // Step 1: Delete the original image
            imageController.deleteImage(filename, new ImageController.ImageDeleteCallback() {
                @Override
                public void onDeleteSuccess() {
                    Log.d(TAG, "Image deleted successfully: " + filename);

                    // Step 2: Replace with placeholder image
                    imageController.copyImage(placeholderPath, filename, new ImageController.ImageDeleteCallback() {
                        @Override
                        public void onDeleteSuccess() {
                            Log.d(TAG, "Placeholder copied successfully to: " + filename);

                            // Step 3: Remove the item from the imageFilenames list
                            imageFilenames.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, imageFilenames.size());

                            Toast.makeText(context, "Image replaced with placeholder.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDeleteFailure(Exception e) {
                            Log.e(TAG, "Failed to replace with placeholder: " + filename, e);
                            Toast.makeText(context, "Failed to replace with placeholder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
