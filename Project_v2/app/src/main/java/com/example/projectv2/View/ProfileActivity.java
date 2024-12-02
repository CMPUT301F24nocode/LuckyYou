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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectv2.Controller.ProfileImageController;
import com.example.projectv2.Utils.topBarUtils;
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

/**
 * Activity for managing user profiles.
 * <p>
 * This activity allows users to view and edit their profile details,
 * manage profile images, handle notification preferences, and display admin-specific settings.
 * </p>
 */
public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView name, email, phoneNumber;
    private Button editProfileButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton editProfilePicButton;
    private ImageView adminStar;
    private CircleImageView profilePic;
    private ProfileImageController imageController;
    private LottieAnimationView profilePicUploadAnimation;

    /**
     * Called when the activity is created.
     * Initializes UI components, retrieves user data, and sets up profile functionalities.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        topBarUtils.topBarSetup(this, "Profile", View.VISIBLE);

        // Initialize UI components
        name = findViewById(R.id.profile_name_box);
        email = findViewById(R.id.profile_email_box);
        phoneNumber = findViewById(R.id.profile_phone_box);
        adminStar = findViewById(R.id.profile_admin_indicator);
        editProfileButton = findViewById(R.id.profile_edit_button);
        swipeRefreshLayout = findViewById(R.id.profile_swipe_refresh);
        editProfilePicButton = findViewById(R.id.profile_pic_edit_button);
        profilePic = findViewById(R.id.profile_pic_view);
        profilePicUploadAnimation = findViewById(R.id.profile_pic_upload_animation);

        db = FirebaseFirestore.getInstance();
        imageController = new ProfileImageController(this);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        boolean adminView = intent.getBooleanExtra("adminView", false);

        if (userID != null) {
            Log.d("ProfileActivity", "userID: " + userID);
            fetchUserData(userID);
        } else {
            Log.e("ProfileActivity", "userID is null");
        }

        // Manage admin view
        if (adminView) {
            editProfilePicButton.setVisibility(View.GONE);
            editProfileButton.setVisibility(View.GONE);
        }

        // More settings button
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> {
            if (adminView) {
                isAdmin(userID, isAdmin -> showAdminProfile(userID, isAdmin));
            } else {
                showPopup(userID);
            }
        });

        // Show admin indicator if applicable
        isAdmin(userID, isAdmin -> {
            if (isAdmin) {
                adminStar.setVisibility(View.VISIBLE);
            }
        });

        // Edit profile button
        editProfileButton.setOnClickListener(view -> {
            if (editProfile(userID)) {
                Snackbar.make(view, "Profile Updated Successfully!", Snackbar.LENGTH_LONG).show();
            }
        });

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userID != null) {
                fetchUserData(userID);
                imageController.loadImage(userID, profilePic);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        // Handle profile image editing
        if (userID != null) {
            imageController.loadImage(userID, profilePic);
        }
        editProfilePicButton.setOnClickListener(v -> {
            imageController.openGallery(this, 1);
        });
    }

    /**
     * Handles the result from the image picker activity.
     *
     * @param requestCode The request code identifying the request.
     * @param resultCode  The result code indicating success or failure.
     * @param data        The intent containing the picked image data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String userID = getIntent().getStringExtra("userID");
            imageController.uploadImageToFirebase(imageUri, userID, new ProfileImageController.ImageUploadCallback() {
                @Override
                public void onSuccess(Uri uri) {
                    Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    imageController.loadImage(userID, profilePic);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    Log.e("ProfileActivity", "Image upload failed", e);
                }
            });
        }
    }

    /**
     * Fetches user data from Firestore and populates the profile fields.
     *
     * @param userID The ID of the user to fetch.
     */
    private void fetchUserData(String userID) {
        Log.d("ProfileActivity", "Fetching user data for userID: " + userID);
        db.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
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
                });
    }

    /**
     * Displays a popup for managing notification preferences and profile picture removal.
     *
     * @param userID The ID of the user to update.
     */
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
                        Log.e("ProfileActivity", "Error updating preferences: " + e.getMessage());
                    });
        });

        removeProfilePicButton.setOnClickListener(v -> {
            imageController.removeImage(userID, profilePic);
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Displays the admin profile overlay dialog.
     *
     * @param userID  The ID of the user.
     * @param isAdmin True if the user is an admin, false otherwise.
     */
    private void showAdminProfile(String userID, boolean isAdmin) {
        AdminProfileOverlayDialog dialog = AdminProfileOverlayDialog.newInstance(userID, isAdmin);
        dialog.show(getSupportFragmentManager(), "AdminProfileOverlayDialog");
    }

    /**
     * Determines if the user is an admin and executes the provided callback.
     *
     * @param userID   The ID of the user.
     * @param callback The callback to execute with the admin status.
     */
    public void isAdmin(String userID, AdminCallback callback) {
        db.collection("Users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        callback.onCallback(isAdmin != null && isAdmin);
                    } else {
                        callback.onCallback(false);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onCallback(false);
                });
    }

    /**
     * Callback interface for determining admin status.
     */
    public interface AdminCallback {
        void onCallback(boolean isAdmin);
    }

    /**
     * Updates the user's profile data in Firestore.
     *
     * @param userID The ID of the user to update.
     * @return True if the profile was updated successfully, false otherwise.
     */
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
            Log.e("ProfileActivity", "Error updating profile", e);
            return false;
        }
    }
}
