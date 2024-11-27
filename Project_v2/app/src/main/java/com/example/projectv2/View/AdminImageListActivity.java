package com.example.projectv2.View;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectv2.Controller.EventImageAdapter;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AdminImageListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventImageAdapter adapter;
    private List<String> imageFilenames;
    private ImageButton backButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns

        imageFilenames = new ArrayList<>();
        adapter = new EventImageAdapter(this, imageFilenames);
        recyclerView.setAdapter(adapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh images when user performs pull-to-refresh gesture
            fetchImages();
        });

        // Initialize Back Button
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Load images initially
        fetchImages();
    }

    private void fetchImages() {
        // Show refresh animation while loading
        swipeRefreshLayout.setRefreshing(true);

        ImageController imageController = new ImageController();
        imageController.getAllEventPosters(new ImageController.ImageListCallback() {
            @Override
            public void onSuccess(List<String> filenames) {
                imageFilenames.clear(); // Clear any existing data
                imageFilenames.addAll(filenames); // Add the filtered filenames
                adapter.notifyDataSetChanged();

                // Hide refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminImageListActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();

                // Hide refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
