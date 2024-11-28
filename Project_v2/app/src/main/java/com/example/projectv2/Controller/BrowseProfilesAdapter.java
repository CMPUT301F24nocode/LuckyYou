package com.example.projectv2.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projectv2.R;
import com.example.projectv2.Model.Profile;

import java.util.List;

public class BrowseProfilesAdapter extends RecyclerView.Adapter<BrowseProfilesAdapter.ViewHolder> {

    private Context context;
    private List<Profile> profiles;

    public BrowseProfilesAdapter(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_profile_list_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile profile = profiles.get(position);

        // Load the profile image using Glide
        Glide.with(context)
                .load(profile.getImageUrl()) // URL or Firebase Storage download URL
                .placeholder(R.drawable.placeholder_profile_picture) // Placeholder image
                .error(R.drawable.placeholder_profile_picture_animated) // Fallback error image
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.imageView);

        holder.nameTextView.setText("Admin- " + profile.getName());
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_image_view);
            nameTextView = itemView.findViewById(R.id.profile_name_text_view);
        }
    }
}
