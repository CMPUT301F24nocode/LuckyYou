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

public class ProfileImageController {
    private final Context context;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    private final DBUtils dbUtils;

    public ProfileImageController(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference().child("profile_pictures");
        this.dbUtils = new DBUtils();
    }

    // Method to open the gallery
    public void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    // Method to upload an image to Firebase Storage
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


    // Method to load an image into an ImageView using Glide
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


    // Interface for callbacks
    public interface ImageUploadCallback {
        void onSuccess(Uri uri);
        void onFailure(Exception e);
    }
    // Method to load a Bitmap into an ImageView using Glide
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
