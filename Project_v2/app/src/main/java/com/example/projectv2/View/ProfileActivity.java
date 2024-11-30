package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectv2.Controller.ProfileImageController;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.Model.User;
import com.example.projectv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView name, email, phoneNumber;
    private Button editProfileButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton editProfilePicButton;
    private CircleImageView profilePic;
    private ProfileImageController imageController;
    private LottieAnimationView profilePicUploadAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        topBarUtils.topBarSetup(this, "Profile", View.VISIBLE);

        name = findViewById(R.id.profile_name_box);
        email = findViewById(R.id.profile_email_box);
        phoneNumber = findViewById(R.id.profile_phone_box);
        editProfileButton = findViewById(R.id.profile_edit_button);
        swipeRefreshLayout = findViewById(R.id.profile_swipe_refresh);
        editProfilePicButton = findViewById(R.id.profile_pic_edit_button);
        profilePic = findViewById(R.id.profile_pic_view);
        profilePicUploadAnimation = findViewById(R.id.profile_pic_upload_animation);

        db = FirebaseFirestore.getInstance();
        imageController = new ProfileImageController(this);

        // Get the intent that was used to start this activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if (userID != null) {
            Log.d("ProfileActivity", "userID: " + userID);
            fetchUserData(userID);
        } else {
            // Handle the case where the userID is not provided
            Log.e("ProfileActivity", "userID is null");
        }

        editProfileButton.setOnClickListener(view -> {
            if (editProfile(userID)) {
                Snackbar.make(view, "Profile Updated Successfully!", Snackbar.LENGTH_LONG).show();
            }
        });

//        fetchUserData(userID);

        // Three dot menu
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup(userID));

        // Set up swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userID != null) {
                fetchUserData(userID);
                imageController.loadImage(userID,profilePic);
            }
            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });

        //Handling profile images

        if (userID != null) {
            imageController.loadImage(userID, profilePic);
        }
        editProfilePicButton.setOnClickListener(v -> {
            imageController.openGallery(this, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
//            profilePicUploadAnimation.setVisibility(View.VISIBLE);
//            profilePicUploadAnimation.playAnimation();
            String userID = getIntent().getStringExtra("userID");
        imageController.loadImage(userID, profilePic);

        imageController.uploadImageToFirebase(imageUri, userID, new ProfileImageController.ImageUploadCallback() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
//                profilePicUploadAnimation.setVisibility(View.GONE);
//                profilePicUploadAnimation.cancelAnimation();
//                imageController.saveImageUriInDB(uri.toString(), userID);
                imageController.loadImage(userID, profilePic);
            }

            @Override
            public void onFailure(Exception e) {
//                profilePicUploadAnimation.setVisibility(View.GONE);
//                profilePicUploadAnimation.cancelAnimation();
Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Image upload failed", e);
            }
        });
        }
    }

    private void fetchUserData(String userID) {
        Log.d("ProfileActivity", "Fetching user data for userID: " + userID);
        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    name.setText(user.getName());
                                    email.setText(user.getEmail());
                                    phoneNumber.setText(user.getPhoneNumber());
                                }
                            }
                        } else {
                            Log.e("ProfileActivity", "Failed to fetch user data: " + task.getException());
                        }

                    }
                });
    }

    private void showPopup(String userID) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.profile_overlay);

        CheckBox needOrganizerNotifs = dialog.findViewById(R.id.profile_notification_organiser_checkbox_view);
        CheckBox needAdminNotifs = dialog.findViewById(R.id.profile_notification_admin_checkbox_view);
        CheckBox adminMode = dialog.findViewById(R.id.profile_admin_mode_checkbox_view);
        Button savePreferencesButton = dialog.findViewById(R.id.save_preferences);
        Button removeProfilePicButton = dialog.findViewById(R.id.remove_profile_pic);
        TextView adminModeText = dialog.findViewById(R.id.adminModeText);

        isAdmin(userID, isAdmin -> {
            if (!isAdmin) {
                adminModeText.setVisibility(View.GONE);
                adminMode.setVisibility(View.GONE);
            }
        });

        AtomicReference<SharedPreferences> preferences = new AtomicReference<>(getSharedPreferences("AppPreferences", MODE_PRIVATE));
        boolean isAdminMode = preferences.get().getBoolean("AdminMode", false); // Default to false if not set

        adminMode.setChecked(isAdminMode);

        if (userID != null) {
            db.collection("Users").document(userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User user = document.toObject(User.class);
                                    if (user != null) {
                                        // Set the checkboxes to current values
                                        needOrganizerNotifs.setChecked(user.isOrganizerNotif());
                                        needAdminNotifs.setChecked(user.isAdminNotif());
                                    }
                                }
                            }

                        }
                    });
        }

        savePreferencesButton.setOnClickListener(v -> {
            boolean newOrganizerNotif = needOrganizerNotifs.isChecked();
            boolean newAdminNotif = needAdminNotifs.isChecked();
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizerNotif", newOrganizerNotif);
            updates.put("adminNotif", newAdminNotif);

            Log.d("AdminCheckbox", "isChecked: " + adminMode.isChecked());
            preferences.set(getSharedPreferences("AppPreferences", MODE_PRIVATE));
            SharedPreferences.Editor editor = preferences.get().edit();
            editor.putBoolean("AdminMode", adminMode.isChecked());
            editor.apply();

            assert userID != null;
            db.collection("Users").document(userID)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Notification preferences updated!",
                                Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Failed to update preferences",
                                Snackbar.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Error updating notification preferences: " + e.getMessage());
                    });
        });
        removeProfilePicButton.setOnClickListener(v -> {
            imageController.removeImage(userID, profilePic);
            dialog.dismiss();
        });

        dialog.show();
    }

    public void isAdmin(String userID, AdminCallback callback) {
        db.collection("Users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        if (isAdmin != null) {
                            callback.onCallback(isAdmin);
                        } else {
                            callback.onCallback(false); // Default to false if admin field is null
                        }
                    } else {
                        callback.onCallback(false); // Document does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onCallback(false); // Return false in case of an error
                });
    }

    public interface AdminCallback {
        void onCallback(boolean isAdmin);
    }

    private boolean editProfile(String userID) {
        try {
            DocumentReference userRef = db.collection("Users").document(userID);
            userRef.update("name", name.getText().toString());
            userRef.update("email", email.getText().toString());
            imageController.loadImage(userID, profilePic);

            String phoneNumberInput = phoneNumber.getText().toString();
            if (!phoneNumberInput.isBlank() && phoneNumberInput.length() != 10) {
                phoneNumber.setError("Phone number should be either empty or 10 digits");
                return false;
            } else {
                userRef.update("phoneNumber", phoneNumberInput);
                return true;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
