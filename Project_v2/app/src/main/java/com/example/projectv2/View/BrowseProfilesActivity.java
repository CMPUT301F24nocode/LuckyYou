package com.example.projectv2.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.BrowseProfilesAdapter;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Profile;
import com.example.projectv2.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BrowseProfilesActivity extends AppCompatActivity implements BrowseProfilesAdapter.OnItemClickListener {

    private static final String TAG = "BrowseProfilesActivity";

    private RecyclerView recyclerView;
    private BrowseProfilesAdapter adapter;
    private List<Profile> profiles;
    private List<StorageReference> storageReferences; // To keep track of storage references

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile_list);
        topBarUtils.topBarSetup(this, "Browse Profiles", View.INVISIBLE);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profiles = new ArrayList<>();
        storageReferences = new ArrayList<>();
        adapter = new BrowseProfilesAdapter(this, profiles, this);
        recyclerView.setAdapter(adapter);

        // Fetch profile data from Firebase Storage
        fetchProfilesFromFirebase();

        // Set up back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Fetches profile data stored in Firebase Storage and updates the RecyclerView.
     */
    private void fetchProfilesFromFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_pictures/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            if (items.isEmpty()) {
                Toast.makeText(BrowseProfilesActivity.this, "No profiles found.", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Number of profiles found: " + items.size());

            // Retrieve download URLs for each profile picture
            for (StorageReference item : items) {
                storageReferences.add(item); // Keep track of the storage reference
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Profile URL: " + uri.toString());
                    // Create a Profile object with the image URL and other data
                    Profile profile = new Profile(uri.toString(), "Name", "Description");
                    profiles.add(profile); // Add profile to list
                    adapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching profile URL: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(BrowseProfilesActivity.this, "Failed to load profiles.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error listing items: " + e.getMessage());
        });
    }

    @Override
    public void onItemClick(int position) {
        Profile profile = profiles.get(position);
        // Show a confirmation dialog to delete the profile
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile(profile, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile(Profile profile, int position) {
        // Delete the profile picture from Firebase Storage
        deleteProfilePictureFromFirebase(profile, position);
    }

    private void deleteProfilePictureFromFirebase(Profile profile, int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_pictures/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            if (items.isEmpty()) {
                Toast.makeText(BrowseProfilesActivity.this, "No profiles found.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (StorageReference item : items) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (uri.toString().equals(profile.getImageUrl())) {
                        item.delete().addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "File deleted successfully: " + profile.getImageUrl());

                            // Remove the profile from the app
                            profiles.remove(position);
                            adapter.notifyItemRemoved(position);

                            // Show a Snackbar for better user feedback
                            Snackbar.make(findViewById(android.R.id.content), "Profile deleted successfully.", Snackbar.LENGTH_SHORT)
                                    .setAction("Undo", view -> {
                                        // Optionally, add functionality to undo the deletion
                                        profiles.add(position, profile);
                                        adapter.notifyItemInserted(position);
                                    })
                                    .show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(BrowseProfilesActivity.this, "Failed to delete profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error deleting file: " + profile.getImageUrl(), e);
                        });
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching profile URL: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(BrowseProfilesActivity.this, "Failed to load profiles.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error listing items: " + e.getMessage());
        });
    }
}

