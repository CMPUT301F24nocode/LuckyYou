/**
 * Activity that displays a list of facilities, allowing users to view existing facilities or create new ones.
 * Retrieves facility data from Firebase Firestore and provides options to add new facilities.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.EventsPagerAdapter;
import com.example.projectv2.Controller.FacilityController;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.Facility;
import com.example.projectv2.R;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * FacilityListActivity displays a list of facilities and allows users to add new facilities.
 * It uses a RecyclerView with a FacilityAdapter to display facilities and handles interactions for creating new facilities.
 */
public class FacilityListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_FACILITY = 1;
    private List<Facility> facilityList;
    private FacilityAdapter facilityAdapter;
    private FacilityController facilityController;

    /**
     * Called when the activity is created. Sets up the RecyclerView for displaying facilities,
     * initializes the FacilityController, and fetches the current list of facilities from Firebase.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_list);

        topBarUtils.topBarSetup(this, "Facilities", View.INVISIBLE);

        // Initialize the list and adapter
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(this, facilityList);

        RecyclerView recyclerView = findViewById(R.id.facility_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(facilityAdapter);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Perform refresh actions, like reloading data
            refreshContent();

            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });

        // Initialize the controller
        facilityController = new FacilityController(this);

        // Fetch facilities from Firebase
        facilityController.fetchFacilities(new FacilityController.FacilityCallback() {
            @Override
            public void onFacilityListLoaded(ArrayList<Facility> facilities) {
                facilityList.clear();
                facilityList.addAll(facilities);
                facilityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FacilityListActivity", "Error fetching facilities", e);
                Toast.makeText(FacilityListActivity.this, "Error loading facilities", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up button to create a new facility
        Button createFacilityButton = findViewById(R.id.create_facility_button);
        createFacilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityCreateActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_FACILITY);
        });
    }

    /**
     * Handles the result from FacilityCreateActivity when a new facility is created.
     * If the result is successful, the new facility data is added to Firebase Firestore and displayed in the list.
     *
     * @param requestCode the integer request code originally supplied to startActivityForResult(), allowing the result code to be identified
     * @param resultCode  the integer result code returned by the child activity through its setResult()
     * @param data        an Intent, which can return result data to the caller (various data can be attached to the Intent "extras")
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_FACILITY && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("facility_name");
            String description = data.getStringExtra("facility_description");
            String facilityID = data.getStringExtra("facility_ID");

            Log.d("FacilityListActivity", "New Facility: " + name + ", " + description);

            // Create and save the new facility in Firebase
            facilityController.createFacility(name, description, facilityID, new FacilityController.FacilityCallback() {
                @Override
                public void onFacilityListLoaded(ArrayList<Facility> facilities) {
                    facilityList.clear();
                    facilityList.addAll(facilities);
                    facilityAdapter.notifyDataSetChanged();
                    Log.d("FacilityListActivity", "Facility list updated with new data");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("FacilityListActivity", "Error adding facility", e);
                    Toast.makeText(FacilityListActivity.this, "Error adding facility", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to handle refresh logic
    private void refreshContent() {
        // Fetch updated facility data
        facilityController.fetchFacilities(new FacilityController.FacilityCallback() {
            @Override
            public void onFacilityListLoaded(ArrayList<Facility> facilities) {
                facilityList.clear();
                facilityList.addAll(facilities);
                facilityAdapter.notifyDataSetChanged();
                Log.d("FacilityListActivity", "Facility list refreshed");
            }

            @Override
            public void onError(Exception e) {
                Log.e("FacilityListActivity", "Error refreshing facilities", e);
                Toast.makeText(FacilityListActivity.this, "Error refreshing facilities", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
