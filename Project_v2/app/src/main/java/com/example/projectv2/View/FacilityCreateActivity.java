/**
 * Activity for creating a new facility. Users can enter the facility name and description,
 * which are then passed back to the calling activity.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;

/**
 * FacilityCreateActivity allows users to create a new facility by entering its name and description.
 * When the facility is created, the data is passed back to the previous activity.
 */
public class FacilityCreateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private EditText nameEditText, descriptionEditText;

    /**
     * Called when the activity is created. Sets up the UI for creating a new facility and
     * initializes top bar and input validation for name and description fields.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_create_page);

        topBarUtils.topBarSetup(this, "Create Facility", View.INVISIBLE);

        nameEditText = findViewById(R.id.facility_create_name_view);
        descriptionEditText = findViewById(R.id.facility_create_description_view);
        Button saveButton = findViewById(R.id.facility_create_button);

        // Configure save button to validate inputs and pass facility data back
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
