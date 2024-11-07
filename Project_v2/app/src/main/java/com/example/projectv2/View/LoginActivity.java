package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.MainActivity;
import com.example.projectv2.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.provider.Settings.Secure;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private TextView signUpButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FirebaseApp.initializeApp(this);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the device ID
        @SuppressLint("HardwareIds")
        String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // Check if user exists in the database
        checkUserExists(deviceID);

        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserExists(String deviceID) {
        Log.d("LoginActivity", "Checking user existence for device ID: " + deviceID);

        db.collection("Users")
                .whereEqualTo(FieldPath.documentId(), deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // User exists
                            Log.d("LoginActivity", "User found with device ID: " + deviceID);
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // No user found
                            Log.d("LoginActivity", "No user found with device ID: " + deviceID);
                            Intent intent = new Intent(this, SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e("LoginActivity", "Error checking user existence: ", task.getException());
                    }
                });
    }
}
