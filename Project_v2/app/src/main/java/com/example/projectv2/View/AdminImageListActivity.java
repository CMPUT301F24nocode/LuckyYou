package com.example.projectv2.Controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectv2.R;

import java.util.Arrays;
import java.util.List;

public class AdminImageListActivity extends AppCompatActivity implements ImageAdapter.OnImageDeleteListener {

    private RecyclerView recyclerViewImages;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        // Sample list of images (assuming they are drawable resources)
        List<Integer> imageList = Arrays.asList(
                R.drawable.placeholder_event, // replace with actual drawable names
                R.drawable.placeholder_event,
                R.drawable.placeholder_event
                // Add more drawable resources as needed
        );

        // Initialize RecyclerView
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        recyclerViewImages.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns

        // Initialize Adapter
        imageAdapter = new ImageAdapter(this, imageList, this);
        recyclerViewImages.setAdapter(imageAdapter);
    }

    @Override
    public void onImageDelete(int position) {
        // Handle image deletion if needed, e.g., showing a dialog or removing from the list
        // For this browsing feature, we might not need to delete images, so this can be empty or show a message
    }
}








