package com.example.projectv2.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.User;
import com.example.projectv2.R;
import com.example.projectv2.Utils.UserUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import android.provider.Settings.Secure;
import android.location.Location;
import android.location.LocationManager;

import java.util.Collections;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseFirestore db;
    private Button signUpButton;
    private EditText email, firstName, lastName, phoneNumber;
    public SignUpActivity() {
        db = FirebaseFirestore.getInstance();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        signUpButton = findViewById(R.id.signup_button);
        email = findViewById(R.id.signup_email);
        firstName = findViewById(R.id.signup_firstname);
        lastName = findViewById(R.id.signup_secondname);
        phoneNumber = findViewById(R.id.signup_phonenumber);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceID=signUpUser();
                if (!Objects.equals(deviceID, "")) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("deviceID", deviceID);
                startActivity(intent);}
            }
        });

    }

    private String signUpUser() {
        int phoneNumberError = isValidPhoneNumber(phoneNumber.getText().toString());
        if (phoneNumberError != 0) {
            if (phoneNumberError == 1) {
                phoneNumber.setError("Phone number must be 10 digits long or empty");
            } else {
                phoneNumber.setError("Phone number must contain only digits");
            }
            return "";

        }
        String deviceID= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        User newUser = new User(email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),phoneNumber.getText().toString(),deviceID);
        newUser.setName(firstName.getText().toString()+" "+lastName.getText().toString());
        db.collection("Users").document(newUser.getDeviceID()).set(newUser).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot added with ID: " + newUser.getDeviceID());
        }).addOnFailureListener(e -> {
            Log.d("User", "Error adding document", e);
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("fcmTokenResult", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and send the token to your server
                    Log.d("fcmTokenResult", "FCM Token: " + token);

                    db.collection("Users")
                            .document(deviceID)
                            .set(Collections.singletonMap("fcmToken", token), SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                // Success callback
                                Log.d("fcmTokenResult", "fcmToken field successfully updated/created!");
                            })
                            .addOnFailureListener(e -> {
                                // Failure callback
                                Log.w("fcmTokenResult", "Error updating/creating fcmToken field", e);
                            });
                });
        return deviceID;
    }
    private int isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is empty or 10 digits long
        if (phoneNumber.isBlank()){
            return 0;
        }else{
            if (phoneNumber.length() != 10) {
                return 1;
            }
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return 2;
            }
        }

        // Phone number is valid
        return 0;
    }
}