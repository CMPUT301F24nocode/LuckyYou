package com.example.projectv2.Controller;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Comparator;

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


    public void uploadImage(Uri imageUri, String eventName, ImageUploadCallback callback) {
        // Sanitize and construct folder path
        String folderName = "event_posters/" + eventName.replaceAll("[^a-zA-Z0-9_]", "_");
        String uniqueFileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageReference.child(folderName + "/" + uniqueFileName);

        Log.d(TAG, "Uploading image to: " + folderName + "/" + uniqueFileName);

        // Upload the file
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
     * @param eventName The name of the event (used to generate folder path).
     * @param callback  Callback for success or failure.
     */
    public void retrieveImage(String eventName, ImageRetrieveCallback callback) {
        // Sanitize and construct folder path
        String folderName = "event_posters/" +"event_posters_"+eventName.replaceAll("[^a-zA-Z0-9_]", "_");
        StorageReference folderRef = storageReference.child(folderName);

        Log.d(TAG, "Retrieving image from folder: " + folderName);

        folderRef.listAll().addOnSuccessListener(listResult -> {
            if (!listResult.getItems().isEmpty()) {
                // Sort files by name (assuming timestamp in filenames) and fetch the latest
                listResult.getItems().stream()
                        .sorted(Comparator.comparing(StorageReference::getName).reversed())
                        .findFirst()
                        .ifPresentOrElse(
                                ref -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Log.d(TAG, "Image retrieved successfully: " + uri.toString());
                                    callback.onRetrieveSuccess(uri.toString());
                                }).addOnFailureListener(callback::onRetrieveFailure),
                                () -> callback.onRetrieveFailure(new Exception("No images found for event: " + eventName))
                        );
            } else {
                callback.onRetrieveFailure(new Exception("No images found for event: " + eventName));
            }
        }).addOnFailureListener(callback::onRetrieveFailure);
    }
}