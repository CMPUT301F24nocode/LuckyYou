package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;

public class FacilityCreateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private EditText nameEditText, descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_create_page);

        topBarUtils.topBarSetup(this, "Create Facility", View.INVISIBLE);

        nameEditText = findViewById(R.id.facility_create_name_view);
        descriptionEditText = findViewById(R.id.facility_create_description_view);
        Button saveButton = findViewById(R.id.facility_create_button);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            // Validate inputs
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the created facility back to FacilityListActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("facility_name", name);
            resultIntent.putExtra("facility_description", description);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
