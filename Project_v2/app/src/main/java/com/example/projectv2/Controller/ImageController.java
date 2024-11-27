package com.example.projectv2.Controller;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageController {
    private static final String TAG = "ImageController";
    private final StorageReference storageReference;

    public ImageController() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    public interface ImageListCallback {
        void onSuccess(List<String> imageUrls);
        void onFailure(Exception e);
    }


    public interface ImageUploadCallback {
        void onUploadSuccess(String downloadUrl);
        void onUploadFailure(Exception e);
    }

    public interface ImageRetrieveCallback {
        void onRetrieveSuccess(String downloadUrl);
        void onRetrieveFailure(Exception e);
    }
    public interface ImageDeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailure(Exception e);
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
        if (eventName == null || eventName.isEmpty()) {
            Log.e(TAG, "Event name is null or empty. Cannot retrieve image.");
            callback.onRetrieveFailure(new IllegalArgumentException("Event name cannot be null or empty."));
            return;
        }
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
    /**
     * Deletes an image and replaces it with the placeholder image.
     */
    public void deleteImage(String filename, ImageDeleteCallback callback) {
        if (filename == null || filename.trim().isEmpty()) {
            callback.onDeleteFailure(new IllegalArgumentException("Filename cannot be null or empty"));
            return;
        }

        StorageReference imageRef = storageReference.child(filename);

        Log.d(TAG, "Attempting to delete file: " + filename);

        imageRef.getMetadata()
                .addOnSuccessListener(metadata -> {
                    imageRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "File deleted successfully: " + filename);
                                callback.onDeleteSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to delete file: " + filename, e);
                                callback.onDeleteFailure(new Exception("Deletion error: " + e.getMessage()));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "File does not exist: " + filename, e);
                    callback.onDeleteFailure(new Exception("File does not exist: " + filename));
                });
    }
    public void getDownloadUrl(String filename, ImageRetrieveCallback callback) {
        StorageReference imageRef = storageReference.child(filename);

        imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> callback.onRetrieveSuccess(uri.toString()))
                .addOnFailureListener(callback::onRetrieveFailure);
    }


    /**
     * Fetches all event posters, excluding placeholder images.
     */
    public void getAllEventPosters(ImageListCallback callback) {
        StorageReference postersRef = storageReference.child("event_posters");

        postersRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<String> imageFilenames = new ArrayList<>();
                    List<StorageReference> items = listResult.getItems();

                    if (items.isEmpty()) {
                        callback.onSuccess(imageFilenames); // Return empty list if no files are found
                        return;
                    }

                    for (StorageReference item : items) {
                        String filename = item.getPath();
                        if (!filename.contains("placeholder_event.png")) {
                            // Exclude placeholder
                            imageFilenames.add(filename);
                        }
                    }

                    callback.onSuccess(imageFilenames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to list files in event_posters folder", e);
                    callback.onFailure(e);
                });
    }


}


