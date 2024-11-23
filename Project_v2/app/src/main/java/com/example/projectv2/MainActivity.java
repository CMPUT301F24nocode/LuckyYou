/**
 * Main activity that serves as the landing page of the app, displaying available events,
 * event status, and user-specific events. The activity also provides navigation options
 * to various features such as profile viewing, facility browsing, and notifications.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectv2.Controller.EventsPagerAdapter;
import com.example.projectv2.Model.User;
import com.example.projectv2.View.AdminFacilityListActivity;
import com.example.projectv2.View.AdminImageListActivity;
import com.example.projectv2.View.AdminProfileListActivity;
import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.CreateEventActivity;
import com.example.projectv2.View.EventLandingPageUserActivity;
import com.example.projectv2.View.FacilityListActivity;
import com.example.projectv2.View.NotificationActivity;
import com.example.projectv2.View.ProfileActivity;
import com.example.projectv2.View.QRUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * MainActivity provides a user interface for accessing and managing events, notifications,
 * user profile, and facilities. Includes a ViewPager for navigating event tabs and a
 * floating action button for creating new events.
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String userName;
    private boolean isOrganizer;

    private static final int REQUEST_CODE_CREATE_EVENT = 2;
    private static final int REQUEST_CODE_QR_SCANNER = 1;

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;

    /**
     * Called when the activity is created. Initializes the ViewPager, navigation drawer, and floating
     * action button. Loads the user profile data and checks organizer status from Firebase Firestore.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressLint("HardwareIds") String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        drawerLayout = findViewById(R.id.homescreen_drawer_layout);
        ImageView profilePicture = findViewById(R.id.homescreen_profile_pic);
        ImageView notificationBell = findViewById(R.id.homescreen_notification_bell);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        viewPager = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        FloatingActionButton fab = findViewById(R.id.homescreen_fab);
        fab.setVisibility(View.VISIBLE);

        fetchAndDisplayUserName(navigationView, userId);

        profilePicture.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Perform refresh actions, like reloading data
            refreshContent();

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });

        notificationBell.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                String userID = getIntent().getStringExtra("deviceID");
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userID", userID);
            } else if(itemId == R.id.nav_qrScanner){
                intent = new Intent(MainActivity.this, QRUserActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCANNER);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            else if (itemId == R.id.nav_facilities) {
                intent = new Intent(MainActivity.this, FacilityListActivity.class);
            } else if (itemId == R.id.nav_browseProfiles) {
                intent = new Intent(MainActivity.this, AdminProfileListActivity.class);
            } else if (itemId == R.id.nav_browseImages) {
                intent = new Intent(MainActivity.this, AdminImageListActivity.class);
            } else if (itemId == R.id.nav_browseFacilities) {
                intent = new Intent(MainActivity.this, AdminFacilityListActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });

        EventsPagerAdapter adapter = new EventsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Available Events");
                    break;
                case 1:
                    tab.setText("Event Status");
                    break;
                case 2:
                    tab.setText("Your Events");
                    break;
            }
        }).attach();
    }

    /**
     * Handles the result from the CreateEventActivity. If a new event is created, it updates
     * the events in the ViewPager without reloading the activity.
     *
     * @param requestCode the integer request code originally supplied to startActivityForResult()
     * @param resultCode  the integer result code returned by the child activity through its setResult()
     * @param data        an Intent, which can return result data to the caller (various data can be attached to the Intent "extras")
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK && viewPager.getCurrentItem() == 0) {
            AvailableEventsFragment fragment = (AvailableEventsFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode==REQUEST_CODE_QR_SCANNER && resultCode==RESULT_OK){
            String qrResult = data.getStringExtra("qrResult");
            Log.d("MainActivity", "QR Result: " + qrResult);
            if (qrResult != null) {
                navigateToEventLandingPage(qrResult);
            }else{
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToEventLandingPage(String qrResult) {
        if (qrResult == null || qrResult.trim().isEmpty()) {
            Toast.makeText(this, "Invalid QR Code: Empty event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, EventLandingPageUserActivity.class);
        intent.putExtra("eventID", qrResult);
        startActivity(intent);
    }

    /**
     * Fetches and displays the user's name in the navigation drawer header.
     *
     * @param navigationView the navigation view containing the drawer layout and header
     * @param userID         the unique identifier of the user (device ID) used to retrieve the user document
     */
    private void fetchAndDisplayUserName(NavigationView navigationView, String userID) {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName = document.getString("name");
                            View headerView = navigationView.getHeaderView(0);
                            TextView userNameTextView = headerView.findViewById(R.id.textView19);
                            userNameTextView.setText(userName);
                        } else {
                            Log.d("MainActivity", "No such document");
                        }
                    } else {
                        Log.d("MainActivity", "get failed with ", task.getException());
                    }
                });
    }

    /**
     * Checks if the user is an organizer. This method is asynchronous and uses a callback to handle
     * the result, which is passed back to the caller.
     *
     * @param userID   the unique identifier of the user (device ID) used to retrieve the user document
     * @param callback the callback interface for handling the result of the organizer check
     */
    private void checkOrganizer(String userID, OnOrganizerCheckComplete callback) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                callback.onComplete(user.isOrganizer());
                            } else {
                                callback.onComplete(false);
                            }
                        } else {
                            callback.onComplete(false);
                        }
                    } else {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * Interface for a callback to handle the result of checking if a user is an organizer.
     */
    interface OnOrganizerCheckComplete {
        /**
         * Called when the organizer check completes.
         *
         * @param isOrganizer true if the user is an organizer, false otherwise
         */
        void onComplete(boolean isOrganizer);
    }

    // Method to handle refresh logic
    private void refreshContent() {
        // Reload your data or update UI
        // For example, fetch data for the ViewPager2
        EventsPagerAdapter adapter = new EventsPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}
