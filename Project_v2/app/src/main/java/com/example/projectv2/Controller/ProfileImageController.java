package com.example.projectv2.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
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
    public void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageView == null) {
            Log.d("ProfileImageController", "Invalid imageUrl or imageView");
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_profile_picture) // Optional placeholder
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable caching
                .skipMemoryCache(true) // Skip memory cache
                .into(imageView);
    }


    // Method to save an image URI locally (e.g., SharedPreferences)
    public void saveImageUriLocally(String imageUri) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profile_image", imageUri);
        editor.apply();
    }

    // Method to retrieve an image URI from local storage
    public String getImageUriLocally() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("profile_image", null);
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
                .into(imageView);
    }

}
