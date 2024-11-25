package com.example.projectv2.Controller;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageController {
    private static final String TAG = "ImageController";
    private final StorageReference storageReference;

    public ImageController() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public interface ImageUploadCallback {
        void onUploadSuccess(String downloadUrl);
        void onUploadFailure(Exception e);
    }

    public interface ImageRetrieveCallback {
        void onRetrieveSuccess(String downloadUrl);
        void onRetrieveFailure(Exception e);
    }

    /**
     * Uploads an image to Firebase Storage with a filename based on the event name.
     *
     * @param imageUri  The URI of the image to be uploaded.
     * @param filePath
     * @param callback  Callback for success or failure.
     */
    public void uploadImage(Uri imageUri, String filePath, ImageUploadCallback callback) {
        // Use the passed filePath directly
        StorageReference imageRef = storageReference.child(filePath);

        Log.d(TAG, "Uploading image to: " + filePath);

        // Upload the file, replacing any existing file at the path
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Image uploaded successfully: " + uri.toString());
                    callback.onUploadSuccess(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed", e);
                    callback.onUploadFailure(e);
                });
    }


    /**
     * Retrieves the download URL of an image based on the event name.
     *
     * @param eventName The name of the event (used to generate the filename).
     * @param callback  Callback for success or failure.
     */
    public void retrieveImage(String eventName, ImageRetrieveCallback callback) {
        // Construct file path: event_posters_<eventName>.jpg
        String fileName = "event_posters/event_posters_" + eventName.replaceAll("[^a-zA-Z0-9_]", "_") + ".jpg";
        StorageReference imageRef = storageReference.child(fileName);

        Log.d(TAG, "Retrieving image from: " + fileName);

        // Retrieve the download URL
        imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "Image retrieved successfully: " + uri.toString());
                    callback.onRetrieveSuccess(uri.toString());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve image", e);
                    callback.onRetrieveFailure(new Exception("No image found for event: " + eventName));
                });
    }
}