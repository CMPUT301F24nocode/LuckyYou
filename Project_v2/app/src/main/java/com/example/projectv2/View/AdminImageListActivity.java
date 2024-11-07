package com.example.projectv2.View;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.ImageAdapter;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class AdminImageListActivity extends AppCompatActivity implements ImageAdapter.OnImageDeleteListener {

    private RecyclerView recyclerViewImages;
    private ImageAdapter imageAdapter;
    private List<Integer> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_list);

        // Initialize the list of images (replace with actual drawable resources)
        imageList = new ArrayList<>();
        imageList.add(R.drawable.placeholder_event); // Replace with actual drawables
        imageList.add(R.drawable.placeholder_event);
        imageList.add(R.drawable.placeholder_event);
        // Add more drawable resources as needed

        // Initialize RecyclerView
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        recyclerViewImages.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns

        // Initialize Adapter with delete listener
        imageAdapter = new ImageAdapter(this, imageList, this);
        recyclerViewImages.setAdapter(imageAdapter);
    }

    @Override
    public void onImageDelete(int position) {
        // Remove the image from the list and notify the adapter
        imageList.remove(position);
        imageAdapter.notifyItemRemoved(position);
    }
}









