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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseProfilesActivity extends AppCompatActivity implements BrowseProfilesAdapter.OnItemClickListener {

    private static final String TAG = "BrowseProfilesActivity";

    private RecyclerView recyclerView;
    private BrowseProfilesAdapter adapter;
    private List<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile_list);
        topBarUtils.topBarSetup(this, "Browse Profiles", View.INVISIBLE);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profiles = new ArrayList<>();
        adapter = new BrowseProfilesAdapter(this, profiles, this);
        recyclerView.setAdapter(adapter);

        // Fetch profile data from Firestore
        fetchProfilesFromFirestore();

        // Set up back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Fetches profile data stored in Firestore and updates the RecyclerView.
     */
    private void fetchProfilesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Profile> profilesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            String imageUrl = document.getString("imageUrl");
                            String description = document.getString("description");
                            Profile profile = new Profile(id, imageUrl, name, description);
                            profilesList.add(profile);
                        }
                        profiles.clear();
                        profiles.addAll(profilesList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(BrowseProfilesActivity.this, "Failed to load profiles.", Toast.LENGTH_SHORT).show();
                    }
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
        // Delete the profile from Firestore
        deleteProfileFromFirestore(profile, position);
    }

    private void deleteProfileFromFirestore(Profile profile, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(profile.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    profiles.remove(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(findViewById(android.R.id.content), "Profile deleted successfully.", Snackbar.LENGTH_SHORT)
                            .setAction("Undo", view -> {
                                profiles.add(position, profile);
                                adapter.notifyItemInserted(position);
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BrowseProfilesActivity.this, "Failed to delete profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error deleting document", e);
                });
    }
}

