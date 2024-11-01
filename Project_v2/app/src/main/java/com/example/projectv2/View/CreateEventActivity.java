package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.MainActivity;
import com.example.projectv2.R;

public class CreateEventActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EVENT_OPTIONS = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;

    private EditText eventNameView, eventDetailsView, eventRulesView;
    private ImageView eventImageView;
    private Uri selectedImageUri; // Store the image URI as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventNameView = findViewById(R.id.create_event_name_view);
        eventDetailsView = findViewById(R.id.create_event_details_view);
        eventRulesView = findViewById(R.id.create_event_rules_view);
        eventImageView = findViewById(R.id.create_event_pic_view);

        // Set up the Back button to return to MainActivity (homescreen)
        ImageButton backButton = findViewById(R.id.event_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to MainActivity
                Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent back stack issues
            }
        });

        // Set up the ImageButton to open the gallery for selecting an image
        ImageButton imageButton = findViewById(R.id.create_event_pic_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        // Set up the Next button to open EventOptionsActivity
        Button nextButton = findViewById(R.id.create_event_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start EventOptionsActivity for result and include the image URI
                Intent intent = new Intent(CreateEventActivity.this, CreateEventOptionsActivity.class);
                intent.putExtra("name", eventNameView.getText().toString());
                intent.putExtra("detail", eventDetailsView.getText().toString());
                intent.putExtra("rules", eventRulesView.getText().toString());

                // Pass the image URI if an image was selected
                if (selectedImageUri != null) {
                    intent.putExtra("imageUri", selectedImageUri.toString());
                }
                startActivityForResult(intent, REQUEST_CODE_EVENT_OPTIONS);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                eventImageView.setImageURI(selectedImageUri); // Display the image in ImageView
            }
        } else if (requestCode == REQUEST_CODE_EVENT_OPTIONS && resultCode == RESULT_OK) {
            // Retrieve the name and details from the EditText fields
            String name = eventNameView.getText().toString();
            String detail = eventDetailsView.getText().toString();
            String rules = eventRulesView.getText().toString();

            if (data != null) {
                data.putExtra("name", name);
                data.putExtra("detail", detail);
                data.putExtra("rules", rules);

                // Include the image URI in the result if available
                if (selectedImageUri != null) {
                    data.putExtra("imageUri", selectedImageUri.toString());
                }
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
