package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.FacilityController;
import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.Facility;
import com.example.projectv2.R;

import java.util.ArrayList;
import java.util.List;

public class FacilityListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_FACILITY = 1;
    private List<Facility> facilityList;
    private FacilityAdapter facilityAdapter;
    private FacilityController facilityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_list);

        // Initialize the list and adapter
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(this, facilityList);

        RecyclerView recyclerView = findViewById(R.id.facility_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(facilityAdapter);

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

        // Set up the Back button to return to MainActivity (homescreen)
        ImageButton backButton = findViewById(R.id.facility_edit_back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacilityListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up button to create a new facility
        Button createFacilityButton = findViewById(R.id.create_facility_button);
        createFacilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityCreateEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_FACILITY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_FACILITY && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("facility_name");
            String description = data.getStringExtra("facility_description");
            Uri imageUri = data.getStringExtra("facility_image") != null ?
                    Uri.parse(data.getStringExtra("facility_image")) : null;

            // Log data to ensure correctness
            Log.d("FacilityListActivity", "New Facility: " + name + ", " + description);

            // Create and save the new facility in Firebase
            facilityController.createFacility(name, description, imageUri, new FacilityController.FacilityCallback() {
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
}