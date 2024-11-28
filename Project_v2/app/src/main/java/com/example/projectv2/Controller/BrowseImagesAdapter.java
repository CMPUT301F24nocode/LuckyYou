package com.example.projectv2.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projectv2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BrowseImagesAdapter extends RecyclerView.Adapter<BrowseImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private List<StorageReference> imageRefs;

    public BrowseImagesAdapter(Context context, List<String> imageUrls, List<StorageReference> imageRefs) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.imageRefs = imageRefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.browse_images_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        StorageReference imageRef = imageRefs.get(position);

        // Load the image using Glide with error handling and placeholders
        Glide.with(context)
                .load(imageUrl) // URL or Firebase Storage download URL
                .placeholder(R.drawable.placeholder_event) // Placeholder image
                .error(R.drawable.placeholder_profile_picture_animated) // Fallback error image
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.imageView);

        // Set a click listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            deleteImage(position, imageRef);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.admin_image_list_object_view); // Reference to the ImageView
            deleteButton = itemView.findViewById(R.id.delete_image_button); // Reference to the delete button
        }
    }

    private void deleteImage(int position, StorageReference imageRef) {
        imageRef.delete().addOnSuccessListener(aVoid -> {
            imageUrls.remove(position);
            imageRefs.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, imageUrls.size());
            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show();
        });
    }
}
