package com.example.projectv2.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectv2.Controller.EventImageAdapter;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AdminImageListActivity extends AppCompatActivity {

    private EventImageAdapter adapter;
    private List<String> imageFilenames;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Initializes the UI layout and sets up the top bar with the title "Browse Images."
     * Initializes the RecyclerView and SwipeRefreshLayout.
     * Fetches and displays the list of images.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        topBarUtils.topBarSetup(this, "Browse Images", View.INVISIBLE);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns

        imageFilenames = new ArrayList<>();
        adapter = new EventImageAdapter(this, imageFilenames);
        recyclerView.setAdapter(adapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        // Refresh images when user performs pull-to-refresh gesture
        swipeRefreshLayout.setOnRefreshListener(this::fetchImages);

        // Load images initially
        fetchImages();
    }

    /**
     * Fetches the list of images from the database and updates the RecyclerView.
     */
    private void fetchImages() {
        // Show refresh animation while loading
        swipeRefreshLayout.setRefreshing(true);

        ImageController imageController = new ImageController();
        imageController.getAllEventPosters(new ImageController.ImageListCallback() {
            // Update the RecyclerView with the list of image filenames
            @Override
            public void onSuccess(List<String> filenames) {
                imageFilenames.clear(); // Clear any existing data
                imageFilenames.addAll(filenames); // Add the filtered filenames
                adapter.notifyDataSetChanged();

                // Hide refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }

            // Show an error message if the image loading fails
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminImageListActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();

                // Hide refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
