package com.example.projectv2.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView name, email, phoneNumber;
    private Button editProfileButton;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        db = FirebaseFirestore.getInstance();

        // Get the intent that was used to start this activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if (userID != null) {
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

        fetchUserData(userID);

        // Three dot menu
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup(userID));

        // Set up swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userID != null) {
                fetchUserData(userID);
            }
            // Stop the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchUserData(String userID) {
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

                                    // Handle phoneNumber as either Long or String
                                    Object phoneObject = document.get("phoneNumber");
                                    if (phoneObject instanceof Long) {
                                        phoneNumber.setText(String.valueOf(phoneObject));
                                    } else if (phoneObject instanceof String) {
                                        phoneNumber.setText((String) phoneObject);
                                    } else {
                                        phoneNumber.setText(""); // Default to empty if null
                                    }
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
        Button savePreferencesButton = dialog.findViewById(R.id.save_preferences);

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

        dialog.show();
    }

    private boolean editProfile(String userID) {
        try {
            DocumentReference userRef = db.collection("Users").document(userID);
            userRef.update("name", name.getText().toString());
            userRef.update("email", email.getText().toString());

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
