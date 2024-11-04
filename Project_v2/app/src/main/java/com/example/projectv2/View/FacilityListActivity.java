package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.R;

public class FacilityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_list);

        // Set up the top bar
        com.example.projectv2.Controller.topBarUtils.topBarSetup(this, "Your Facilities", View.INVISIBLE);

        // Find and set up the create facility button
        Button createFacilityButton = findViewById(R.id.create_facility_button);
        createFacilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityCreateEditActivity.class);
            startActivity(intent);
        });
    }
}
