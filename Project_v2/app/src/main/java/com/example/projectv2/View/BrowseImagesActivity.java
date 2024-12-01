package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.BrowseImagesAdapter;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BrowseImagesActivity extends AppCompatActivity {

    private static final String TAG = "BrowseImagesActivity";

    private RecyclerView recyclerView;
    private BrowseImagesAdapter adapter;
    private List<String> imageUrls;
    private List<StorageReference> imageRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_images);


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns

        imageUrls = new ArrayList<>();
        imageRefs = new ArrayList<>();
        adapter = new BrowseImagesAdapter(this, imageUrls, imageRefs);
        recyclerView.setAdapter(adapter);

        // Fetch image URLs from Firebase Storage
        fetchImagesFromFirebase();
    }

    /**
     * Fetches image URLs stored in Firebase Storage and updates the RecyclerView.
     */
    private void fetchImagesFromFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("event_posters");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            if (items.isEmpty()) {
                Toast.makeText(BrowseImagesActivity.this, "No images found.", Toast.LENGTH_SHORT).show();
                return;
            }

            imageRefs.clear();
            imageUrls.clear();
            imageRefs.addAll(items);

            // Retrieve download URLs for each image
            for (StorageReference item : items) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString()); // Add URL to list
                    adapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching image URL: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(BrowseImagesActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error listing items: " + e.getMessage());
        });
    }

}
