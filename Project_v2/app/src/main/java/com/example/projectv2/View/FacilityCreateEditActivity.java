package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.Model.Facility;
import com.example.projectv2.R;

public class FacilityCreateEditActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private EditText nameEditText, descriptionEditText;
    private ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_create_page);

        // Back button to navigate back to the previous activity
        ImageButton eventBackButton = findViewById(R.id.facility_edit_back_button);
        eventBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Ends the current activity and returns to the previous screen
            }
        });

        nameEditText = findViewById(R.id.facility_edit_name_view);
        descriptionEditText = findViewById(R.id.facility_edit_description_view);
        imageView = findViewById(R.id.create_event_pic_view);
        ImageButton pickImageButton = findViewById(R.id.create_event_pic_button);
        Button saveButton = findViewById(R.id.facility_edit_button);

        pickImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

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
            resultIntent.putExtra("facility_image", imageUri != null ? imageUri.toString() : null);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                imageView.setImageURI(imageUri);
            }
        }
    }
}
