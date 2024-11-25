package com.example.projectv2.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.R;

public class EventEditActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 3;
    private ImageView eventPosterImageView;
    private Uri selectedImageUri;
    private String eventName;
    private ImageController imageController;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_poster);

        eventPosterImageView = findViewById(R.id.create_event_pic_view);
        Button editPosterButton = findViewById(R.id.create_event_next_button);

        imageController = new ImageController();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Retrieve event name from intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Invalid event name", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing event poster
        loadEventPoster();

        // Set up button to select a new image
        editPosterButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });
    }

    /**
     * Loads the current event poster from Firebase Storage.
     */
    private void loadEventPoster() {
        progressDialog.setMessage("Loading event poster...");
        progressDialog.show();

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                progressDialog.dismiss();
                Glide.with(EventEditActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .into(eventPosterImageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EventEditActivity.this, "Failed to load event poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                eventPosterImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }

    /**
     * Handles the result of the image selection.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                eventPosterImageView.setImageURI(selectedImageUri);
                updateEventPoster(); // Update the poster with the selected image
            }
        }
    }

    /**
     * Updates the event poster in Firebase Storage.
     */
    /**
     * Updates the event poster in Firebase Storage.
     */
    private void updateEventPoster() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating event poster...");
        progressDialog.show();

        // Construct file path: event_posters_<eventName>.jpg
        String filePath = "event_posters/event_posters_" + eventName.replaceAll("[^a-zA-Z0-9_]", "_") + ".jpg";

        // Upload the new image and overwrite the existing file
        imageController.uploadImage(selectedImageUri, filePath, new ImageController.ImageUploadCallback() {
            @Override
            public void onUploadSuccess(String downloadUrl) {
                progressDialog.dismiss();
                Toast.makeText(EventEditActivity.this, "Poster updated successfully", Toast.LENGTH_SHORT).show();

                // Refresh the image in the ImageView
                Glide.with(EventEditActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .skipMemoryCache(true) // Bypass memory cache
                        .into(eventPosterImageView);
                finish();
            }

            @Override
            public void onUploadFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EventEditActivity.this, "Failed to update poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
