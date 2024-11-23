package com.example.projectv2.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import android.provider.Settings.Secure;
import android.location.Location;
import android.location.LocationManager;

public class SignUpActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseFirestore db;
    private Button signUpButton;
    private EditText email, firstName, lastName, phoneNumber;
    private CheckBox isOrganizer;
    private double latitude, longitude;
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

        // Request location permission
        requestLocationPermission();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success=signUpUser();
                if (success) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);}
            }
        });

    }

    /**
     * Requests location permission if not already granted.
     */
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchCurrentLocation();
        }
    }

    /**
     * Fetches the user's current location.
     */
    private void fetchCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("Location", "Fetched location: (" + latitude + ", " + longitude + ")");
                } else {
                    Log.w("Location", "No location data available. Using defaults (0, 0).");
                    latitude = 0.0;
                    longitude = 0.0;
                }
            }).addOnFailureListener(e -> {
                Log.e("Location", "Error fetching location.", e);
                latitude = 0.0;
                longitude = 0.0;
            });
        } catch (SecurityException e) {
            Log.e("Location", "SecurityException: Missing location permissions.", e);
        }
    }

    /**
     * Handles the result of the location permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean signUpUser() {
        int phoneNumberError = isValidPhoneNumber(phoneNumber.getText().toString());
        if (phoneNumberError != 0) {
            if (phoneNumberError == 1) {
                phoneNumber.setError("Phone number must be 10 digits long or empty");
                return false;
            } else {
                phoneNumber.setError("Phone number must contain only digits");
                return false;
            }

        }
        @SuppressLint("HardwareIds") String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        User newUser = new User(email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),phoneNumber.getText().toString(),deviceID);
        // Set user location
        newUser.setLatitude(latitude);
        newUser.setLongitude(longitude);
        newUser.setName(firstName.getText().toString()+" "+lastName.getText().toString());
        db.collection("Users").document(newUser.getDeviceID()).set(newUser).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot added with ID: " + newUser.getDeviceID());
        }).addOnFailureListener(e -> {
            Log.d("User", "Error adding document", e);
        });
        return true;

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