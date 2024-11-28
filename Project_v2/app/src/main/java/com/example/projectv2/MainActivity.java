package com.example.projectv2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectv2.Controller.EventsPagerAdapter;
import com.example.projectv2.R;
import com.example.projectv2.View.AdminFacilityListActivity;
import com.example.projectv2.View.AdminProfileListActivity;
import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.BrowseImagesActivity;
import com.example.projectv2.View.CreateEventActivity;
import com.example.projectv2.View.EventLandingPageUserActivity;
import com.example.projectv2.View.FacilityListActivity;
import com.example.projectv2.View.NotificationActivity;
import com.example.projectv2.View.QRUserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore db;
    private String userName;
    private boolean isOrganizer;

    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private static final int REQUEST_CODE_QR_SCANNER = 2;

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressLint("HardwareIds") String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
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
        navigationView.setNavigationItemSelectedListener(this);

        fetchAndDisplayUserName(navigationView, userId);

        profilePicture.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshContent();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        if (itemId == R.id.nav_browseImages) {
            intent = new Intent(this, BrowseImagesActivity.class);
        } else if (itemId == R.id.nav_qrScanner) {
            intent = new Intent(this, QRUserActivity.class);
            startActivityForResult(intent, REQUEST_CODE_QR_SCANNER);
            return true;
        } else if (itemId == R.id.nav_facilities) {
            intent = new Intent(this, FacilityListActivity.class);
        } else if (itemId == R.id.nav_browseEvents) {
            intent = new Intent(this, AdminFacilityListActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

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

    private void navigateToEventLandingPage(String qrResult) {
        if (qrResult == null || qrResult.trim().isEmpty()) {
            Toast.makeText(this, "Invalid QR Code: Empty event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, EventLandingPageUserActivity.class);
        intent.putExtra("eventID", qrResult);
        startActivity(intent);
    }

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

    private void refreshContent() {
        EventsPagerAdapter adapter = new EventsPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}
