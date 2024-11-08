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

import com.example.projectv2.Controller.EventsPagerAdapter;
import com.example.projectv2.Model.User;
import com.example.projectv2.View.AdminFacilityListActivity;
import com.example.projectv2.View.AdminImageListActivity;
import com.example.projectv2.View.AdminProfileListActivity;
import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.CreateEventActivity;
import com.example.projectv2.View.FacilityListActivity;
import com.example.projectv2.View.NotificationActivity;
import com.example.projectv2.View.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
//    private TextView userNameTextView;
    private String userName;
    private boolean isOrganizer;


    private static final int REQUEST_CODE_CREATE_EVENT = 1;

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtain the user's device ID for identification
        @SuppressLint("HardwareIds") String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        NavigationView navigationView ;



        drawerLayout = findViewById(R.id.homescreen_drawer_layout);
        ImageView profilePicture = findViewById(R.id.homescreen_profile_pic);
        ImageView notificationBell = findViewById(R.id.homescreen_notification_bell);
        navigationView = findViewById(R.id.navigation_view);
        viewPager = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FloatingActionButton fab = findViewById(R.id.homescreen_fab);
        fab.setVisibility(View.VISIBLE);
//        checkOrganizer(userId, new OnOrganizerCheckComplete() {
//            @Override
//            public void onComplete(boolean isOrganizerResult) {
//                isOrganizer = isOrganizerResult;
//                Log.d("isOrganizer", String.valueOf(isOrganizer));
//
//                // Update UI based on organizer status
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isOrganizer) {
//                            fab.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//            }
//        });


        fetchAndDisplayUserName(navigationView,userId);

        profilePicture.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
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
                //User should see their details
                Intent intent2 = getIntent();
                String userID = intent2.getStringExtra("deviceID");
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userID", userID);
            } else if (itemId == R.id.nav_facilities) {
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

    // Handle the result from EventCreatorActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the result to the fragment
        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK && viewPager.getCurrentItem() == 0) {
            AvailableEventsFragment fragment = (AvailableEventsFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        }
    }
    private void fetchAndDisplayUserName(NavigationView navigationView,String userID) {
        // Get the current user's ID (assuming you have it stored somewhere)


        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the user's name from the document
                                userName = document.getString("name");
                                // Update the TextView with the user's name
                                View headerView = navigationView.getHeaderView(0);
                                TextView userNameTextView = headerView.findViewById(R.id.textView19);
                                userNameTextView.setText(userName);
                            } else {
                                Log.d("MainActivity", "No such document");
                            }
                        } else {
                            Log.d("MainActivity", "get failed with ", task.getException());
                        }
                    }
                });
    }
    //The checkOrganizer method returns the value of isOrganizer before the Firestore query completes. because of the asynchoronous nature of Firestore
    private void checkOrganizer(String userID, OnOrganizerCheckComplete callback) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                Log.d("MainActivity", "Fetched the class");

                                if (user != null) {
                                    boolean isOrganizer = user.isOrganizer();
                                    Log.d("checkOrganizer", String.valueOf(isOrganizer));
                                    callback.onComplete(isOrganizer);
                                } else {
                                    callback.onComplete(false);
                                }
                            } else {
                                callback.onComplete(false);
                            }
                        } else {
                            callback.onComplete(false);
                        }
                    }
                });
    }

    // Create an interface for the callback
    interface OnOrganizerCheckComplete {
        void onComplete(boolean isOrganizer);
    }
}
