package com.example.projectv2.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projectv2.Utils.DBUtils;
import com.example.projectv2.Utils.ProfilePictureGenerator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.projectv2.R;

/**
 * Controller class for handling profile image operations in Firebase Storage.
 */
public class ProfileImageController {
    private final Context context;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    private final DBUtils dbUtils;

    /**
     * Constructs a ProfileImageController with the specified context.
     *
     * @param context the context in which the controller is operating
     */
    public ProfileImageController(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference().child("profile_pictures");
        this.dbUtils = new DBUtils();
    }

    /**
     * Opens the gallery to select an image.
     *
     * @param activity    the activity in which the gallery is opened
     * @param requestCode the request code for the gallery activity
     */
    public void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Uploads an image to Firebase Storage with a filename based on the user ID.
     *
     * @param imageUri the URI of the image to be uploaded
     * @param userID   the user ID to use in the filename
     * @param callback callback for success or failure
     */
    public void uploadImageToFirebase(Uri imageUri, String userID, ImageUploadCallback callback) {
        // Define the file path using userID
        String filePath = "user_" + userID + ".jpg";
        StorageReference fileRef = storageRef.child(filePath);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(callback::onSuccess)
                )
                .addOnFailureListener(callback::onFailure);
        dbUtils.fetchUser(userID, user -> {
            if(user!=null){
                user.setProfileImage(filePath);
                dbUtils.updateUser(userID,user);
            }
        });

    }

    /**
     * Loads an image from Firebase Storage based on the user ID and sets it in the specified ImageView.
     *
     * @param userID    the user ID to use in the filename
     * @param imageView the ImageView in which to set the image
     */
    public void loadImage(String userID, ImageView imageView) {
        if (userID == null || imageView == null) {
            Log.d("HUHUUUUU", "Invalid imageUrl or imageView");
            return;
        }

        // Ensure context is valid and Activity is not destroyed
        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
            Log.e("ProfileImageController", "Activity is destroyed. Skipping image load.");
            return;
        }
        String filePath = "user_" + userID + ".jpg";
        StorageReference fileRef = storageRef.child(filePath);
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("ProfileImageController", "Image URL: " + uri.toString());
            Glide.with(context)
                    .load(uri.toString())
                    .placeholder(R.drawable.placeholder_profile_picture) // Optional placeholder
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable caching
                    .skipMemoryCache(true) // Skip memory cache
                    .into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("ProfileImageController", "Error loading image: " + e.getMessage());
            dbUtils.fetchUser(userID, user -> {
                if (user!=null){
                    String userName = user.getName();
            Bitmap genProfilePicture = ProfilePictureGenerator.generateProfilePicture( userName, 500);
            this.loadImageUsingBitmap(genProfilePicture, imageView);}});
        });


    }

    /**
     * Removes an image from Firebase Storage based on the user ID.
     *
     * @param userID    the user ID to use in the filename
     * @param imageView the ImageView in which to set the default image
     */
    public void removeImage(String userID, ImageView imageView) {
        if (userID == null || imageView == null) {
            Log.d("HUHUUUUU", "Invalid imageUrl or imageView");
            return;
        }
        String filePath = "user_" + userID + ".jpg";
        StorageReference fileRef = storageRef.child(filePath);
        fileRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("ProfileImageController", "Image deleted successfully");
            dbUtils.fetchUser(userID, user -> {
                if (user!=null){
                    Toast.makeText(context, "Image Deleted!", Toast.LENGTH_SHORT).show();
                    String userName = user.getName();
                    Bitmap genProfilePicture = ProfilePictureGenerator.generateProfilePicture( userName, 500);
                    this.loadImageUsingBitmap(genProfilePicture, imageView);}});
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "No Image To Delete!", Toast.LENGTH_SHORT).show();
        });


    }

    /**
     * Callback interface for image upload operations.
     */
    public interface ImageUploadCallback {
        void onSuccess(Uri uri);
        void onFailure(Exception e);
    }

    /**
     * Loads an image into an ImageView using a Bitmap.
     *
     * @param bitmap    the Bitmap to load
     * @param imageView the ImageView in which to load the Bitmap
     */
    public void loadImageUsingBitmap(Bitmap bitmap, ImageView imageView) {
        if (bitmap == null || imageView == null) {
            Log.d("ProfileImageController", "Invalid bitmap or imageView");
            return;
        }

        Glide.with(context)
                .load(bitmap)
                .placeholder(R.drawable.placeholder_profile_picture) // Optional placeholder
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching
                .skipMemoryCache(true) // Disable memory caching
                .into(imageView);
    }

}
