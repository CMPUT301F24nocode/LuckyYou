/**
 * Main activity that serves as the landing page of the app, displaying available events,
 * event status, and user-specific events. The activity also provides navigation options
 * to various features such as profile viewing, facility browsing, and notifications.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectv2.Utils.DBUtils;
import com.example.projectv2.Controller.DeadlineWorker;
import com.example.projectv2.Controller.EventsPagerAdapter;
import com.example.projectv2.Controller.ProfileImageController;
import com.example.projectv2.Model.User;
import com.example.projectv2.View.AdminEventListActivity;
import com.example.projectv2.View.AdminImageListActivity;
import com.example.projectv2.View.AdminProfileListActivity;
import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.CreateEventActivity;
import com.example.projectv2.View.EventLandingPageUserActivity;
import com.example.projectv2.View.FacilityListActivity;
import com.example.projectv2.View.NotificationActivity;
import com.example.projectv2.View.ProfileActivity;
import com.example.projectv2.View.QRUserActivity;
import com.example.projectv2.View.SplashScreenActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * MainActivity provides a user interface for accessing and managing events, notifications,
 * user profile, and facilities. Includes a ViewPager for navigating event tabs and a
 * floating action button for creating new events.
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String userName;
    private boolean isOrganizer;
    private DBUtils dbUtils;
    private ProfileImageController profileImageController;
    private CircleImageView profilePic, profilePicture;

    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private static final int REQUEST_CODE_QR_SCANNER = 2;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

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

        // Deadline Execution
        OneTimeWorkRequest immediateWorkRequest = new OneTimeWorkRequest.Builder(
                DeadlineWorker.class).build();
        WorkManager.getInstance(this).enqueue(immediateWorkRequest);

        // Periodic work for subsequent executions
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                DeadlineWorker.class,
                15, TimeUnit.MINUTES
        ).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MoveExpiredUsers",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );

        drawerLayout = findViewById(R.id.homescreen_drawer_layout);
        profilePicture = findViewById(R.id.homescreen_profile_pic);
        ImageView notificationBell = findViewById(R.id.homescreen_notification_bell);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        viewPager = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        FloatingActionButton fab = findViewById(R.id.homescreen_fab);
        fab.setVisibility(View.VISIBLE);
        dbUtils = new DBUtils();
        profileImageController = new ProfileImageController(this);
        profilePic = navigationView.getHeaderView(0).findViewById(R.id.profile_pic_view);
        profilePicture.setImageResource(R.drawable.app_logo);

        fetchAndDisplayUserNameAndImage(navigationView, userId);

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
            refreshContent(navigationView, userId);

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });

        notificationBell.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();

        db.collection("facilities")
                .whereEqualTo("owner", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean hasFacilities = task.getResult() != null && !task.getResult().isEmpty();
                        if (hasFacilities) {
                            fab.setVisibility(View.VISIBLE);
                            fab.setOnClickListener(view -> {
                                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
                            });
                        } else {
                            fab.setOnClickListener(view -> Toast.makeText(MainActivity.this, "Please make a facility.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e("FABListener", "Error fetching facilities: ", task.getException());
                        Toast.makeText(MainActivity.this, "Failed to check facilities.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Initialize SharedPreferences
        preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("AdminMode", false);
        editor.apply();

        // Access the navigation menu
        Menu menu = navigationView.getMenu();

        // Register the listener for SharedPreferences changes
        preferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals("AdminMode")) {
                boolean isAdminMode = sharedPreferences.getBoolean("AdminMode", false);

                // Update visibility of admin-specific menu items
                menu.findItem(R.id.nav_browseProfiles).setVisible(isAdminMode);
                menu.findItem(R.id.nav_browseImages).setVisible(isAdminMode);
                menu.findItem(R.id.nav_browseEvents).setVisible(isAdminMode);

                // Log for debugging
                Log.d("AdminCheckbox", "isAdminMode: " + isAdminMode);
            }
        };

        // Register the listener
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        // Set initial visibility based on saved preference
        boolean isAdminMode = preferences.getBoolean("AdminMode", false);
        menu.findItem(R.id.nav_browseProfiles).setVisible(isAdminMode);
        menu.findItem(R.id.nav_browseImages).setVisible(isAdminMode);
        menu.findItem(R.id.nav_browseEvents).setVisible(isAdminMode);

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                String userID = getIntent().getStringExtra("userID");
                intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                intent.putExtra("message", "Curating Your Profile!");
                intent.putExtra("TARGET_ACTIVITY", ProfileActivity.class.getName());
                Bundle extras = new Bundle();
                extras.putString("userID", userID);
                intent.putExtra("EXTRA_DATA", extras);
            } else if(itemId == R.id.nav_qrScanner){
                intent = new Intent(MainActivity.this, QRUserActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCANNER);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (itemId == R.id.nav_facilities) {
                intent = new Intent(MainActivity.this, FacilityListActivity.class);
            } else if (itemId == R.id.nav_browseProfiles) {
                intent = new Intent(MainActivity.this, AdminProfileListActivity.class);
            } else if (itemId == R.id.nav_browseImages) {
                intent = new Intent(MainActivity.this, AdminImageListActivity.class);
            } else if (itemId == R.id.nav_browseEvents) {
                intent = new Intent(MainActivity.this, AdminEventListActivity.class);
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
        // Check and request notification permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request the POST_NOTIFICATIONS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    /**
     * Called when the activity is resumed. Checks if the user is an organizer and updates the
     * floating action button visibility accordingly.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
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
        } else if (requestCode == REQUEST_CODE_QR_SCANNER && resultCode == RESULT_OK) {
            String qrResult = data.getStringExtra("qrResult");
            Log.d("MainActivity", "QR Result: " + qrResult);
            if (qrResult != null) {
                navigateToEventLandingPage(qrResult);
            } else {
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Navigates to the event landing page using the event ID obtained from the QR code.
     *
     * @param qrResult the event ID obtained from the QR code
     */
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
    private void fetchAndDisplayUserNameAndImage(NavigationView navigationView, String userID) {
        dbUtils.fetchUser(userID, user -> {

            if (user != null) {
                // Set user's name
                userName = user.getName();
                Log.d("BLAH", userName);

                View headerView = navigationView.getHeaderView(0);
                TextView userNameTextView = headerView.findViewById(R.id.textView19);
                userNameTextView.setText(userName);
                if (!Objects.equals(userID, "")) {
                    Log.d("HUHUUUUU",userID);
                    profileImageController.loadImage(userID, profilePicture);
                    Log.d("HUHUUUUU","Tried to call loadimage on profilePicture "+userID);
                    profileImageController.loadImage(userID, profilePic);}
                else {
                    Log.d("HUHUUUUU","userID is null");
                }


            } else {
                Log.d("BLAH", "User not found in Firestore");
            }
        });
    }

    // Method to handle refresh logic
    private void refreshContent(NavigationView navigationView, String userId) {
        // Reload your data or update UI
        // For example, fetch data for the ViewPager2
        EventsPagerAdapter adapter = new EventsPagerAdapter(this);
        viewPager.setAdapter(adapter);
        fetchAndDisplayUserNameAndImage(navigationView, userId);
    }
}
