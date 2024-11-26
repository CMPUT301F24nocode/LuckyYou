package com.example.projectv2.View;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns

        imageFilenames = new ArrayList<>();
        adapter = new EventImageAdapter(this, imageFilenames);
        recyclerView.setAdapter(adapter);

        fetchImages();
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Finish the activity and navigate back
            finish();
        });
    }

    private void fetchImages() {
        ImageController imageController = new ImageController();
        imageController.getAllEventPosters(new ImageController.ImageListCallback() {
            @Override
            public void onSuccess(List<String> filenames) {
                imageFilenames.clear(); // Clear any existing data
                imageFilenames.addAll(filenames); // Add the filtered filenames
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminImageListActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}