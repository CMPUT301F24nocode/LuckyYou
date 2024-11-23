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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView name, email, phoneNumber;
    private Button editProfileButton;
//    private CheckBox needOrganizerNotifs, needAdminNotifs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        topBarUtils.topBarSetup(this, "Profile", View.VISIBLE);

        name = findViewById(R.id.profile_name_box);
        email = findViewById(R.id.profile_email_box);
        phoneNumber = findViewById(R.id.profile_phone_box);
        editProfileButton = findViewById(R.id.profile_edit_button);


        db=FirebaseFirestore.getInstance();

        // Get the intent that was used to start this activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if (userID != null) {
            fetchUserData(userID);
        } else {
            // Handle the case where the userID is not provided
            Log.e("ProfileActivity", "userID is null");
        }
        editProfileButton.setOnClickListener(view-> {

                if (editProfile(userID)){
                    Snackbar.make(view, "Profile Updated Successfully!", Snackbar.LENGTH_LONG).show();}
                }
                );


//        fetchUserData();
        //three dot menu
        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup(userID));


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
                                    phoneNumber.setText(String.valueOf(user.getPhoneNumber()));
                                }
                            }
                        }
                    }
                });
    }

    private void showPopup(String userID){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.profile_overlay);
        CheckBox needOrganizerNotifs=dialog.findViewById(R.id.profile_notification_organiser_checkbox_view);
        CheckBox needAdminNotifs=dialog.findViewById(R.id.profile_notification_admin_checkbox_view);
        Button savePreferencesButton=dialog.findViewById(R.id.save_preferences);
        if (userID!=null) {
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
        savePreferencesButton.setOnClickListener(v->{
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

    private boolean editProfile(String userID){
        try{DocumentReference userRef = db.collection("Users").document(userID);
        userRef.update("name", name.getText().toString());
        userRef.update("email", email.getText().toString());
        long phone;
        try{
        phone = Long.parseLong(phoneNumber.getText().toString());}catch (NumberFormatException e){
            Log.e("ProfileActivity", "Invalid phone number format");
            phone=0;

        }

        userRef.update("phoneNumber", phone); return true;} catch (Exception e){
            Log.e("ProfileActivity", "Error updating user profile: " + e.getMessage());
            return false;
        }


    }
//    private void updateUser(User user){
//        User newUser = new User(user.isAdmin(),user.isAdminNotif(),user.getEmail(),user.getName(),user.getPhoneNumber());

  //  }
}
