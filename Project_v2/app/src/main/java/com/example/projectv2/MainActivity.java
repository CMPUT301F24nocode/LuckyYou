package com.example.projectv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.Toast;

import com.example.projectv2.View.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_EVENT = 1;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sign_up);
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);

//        drawerLayout = findViewById(R.id.homescreen_drawer_layout);
//        ImageView profilePicture = findViewById(R.id.homescreen_profile_pic);
//        ImageView notificationBell = findViewById(R.id.homescreen_notification_bell);
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        ViewPager2 viewPager = findViewById(R.id.viewPager2);
//        TabLayout tabLayout = findViewById(R.id.tabLayout);
//        FloatingActionButton fab = findViewById(R.id.homescreen_fab);
//
//        profilePicture.setOnClickListener(view -> {
//            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//
//        notificationBell.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
//            startActivity(intent);
//        });
//
//        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT); // Start CreateEventActivity for result
//        });
//
//        navigationView.setNavigationItemSelectedListener(item -> {
//            Intent intent = null;
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_profile) {
//                intent = new Intent(MainActivity.this, ProfileActivity.class);
//            } else if (itemId == R.id.nav_facilities) {
//                intent = new Intent(MainActivity.this, FacilityListActivity.class);
//            } else if (itemId == R.id.nav_browseProfiles) {
//                intent = new Intent(MainActivity.this, AdminProfileListActivity.class);
//            } else if (itemId == R.id.nav_browseImages) {
//                intent = new Intent(MainActivity.this, AdminImageListActivity.class);
//            } else if (itemId == R.id.nav_browseFacilities) {
//                intent = new Intent(MainActivity.this, AdminFacilityListActivity.class);
//            }
//
//            if (intent != null) {
//                startActivity(intent);
//                drawerLayout.closeDrawer(GravityCompat.START);
//            }
//            return true;
//        });
//
//        EventsPagerAdapter adapter = new EventsPagerAdapter(this);
//        viewPager.setAdapter(adapter);
//
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            switch (position) {
//                case 0:
//                    tab.setText("Available Events");
//                    break;
//                case 1:
//                    tab.setText("Event Status");
//                    break;
//                case 2:
//                    tab.setText("Your Events");
//                    break;
//            }
//        }).attach();
    }
}
