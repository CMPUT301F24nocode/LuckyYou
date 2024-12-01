/**
 * Activity for displaying the list of user profiles in the admin view.
 * Sets up the top bar with a title and back button functionality.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectv2.Controller.AdminProfileAdapter;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.Model.User;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AdminProfileListActivity displays the list of user profiles available for admin users to browse.
 * It initializes the UI layout and sets up the top bar with the title "Browse Profiles."
 */
public class AdminProfileListActivity extends AppCompatActivity {

    private AdminProfileAdapter adapter;
    private List<User> userList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile_list);

        topBarUtils.topBarSetup(this, "Browse Profiles", View.INVISIBLE);

        RecyclerView adminProfileListRecycler = findViewById(R.id.adminProfileListRecycler);
        adminProfileListRecycler.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new AdminProfileAdapter(userList, (userID) -> showProfile(userID));
        adminProfileListRecycler.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadUsersFromFirestore();
            swipeRefreshLayout.setRefreshing(false);
        });

        loadUsersFromFirestore();
    }

    private void loadUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user.getName() != null) {
                            user.setDeviceID(document.getId()); // Set userID
                            userList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show());
    }

    private void showProfile(String userID) {
        String deviceID= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!Objects.equals(deviceID, userID)) {
            Intent intent = new Intent(AdminProfileListActivity.this, ProfileActivity.class);

            intent.putExtra("userID", userID);
            intent.putExtra("adminView", true);

            startActivity(intent);
        } else {
            Toast.makeText(this, "This is your account", Toast.LENGTH_SHORT).show();
        }
    }
}
