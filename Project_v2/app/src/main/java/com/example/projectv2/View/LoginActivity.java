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
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Get the device ID
        @SuppressLint("HardwareIds")
        String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Log.d(TAG, "Device ID obtained: " + deviceID);

        // Check if user exists in the database
        checkUserExists(deviceID);

        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserExists(String deviceID) {
        Log.d(TAG, "Starting user existence check for device ID: " + deviceID);

        // First, try direct document lookup
        db.collection("Users")
                .document(deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "Document lookup completed. Exists: " + document.exists());

                        if (document.exists()) {
                            // Document exists - log its data
                            Log.d(TAG, "Document data: " + document.getData());

                            // Navigate to MainActivity
                            Log.d(TAG, "Navigating to MainActivity");
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Document doesn't exist - try query as fallback
                            Log.d(TAG, "Document doesn't exist, trying query fallback");
                            queryUserCollection(deviceID);
                        }
                    } else {
                        Log.e(TAG, "Error getting document: ", task.getException());
                        // Show error to user
                        Toast.makeText(this, "Error checking user status: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void queryUserCollection(String deviceID) {
        db.collection("Users")
                .whereEqualTo(FieldPath.documentId(), deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        Log.d(TAG, "Query completed. Empty: " + querySnapshot.isEmpty() +
                                " Size: " + querySnapshot.size());

                        if (!querySnapshot.isEmpty()) {
                            // User exists
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            Log.d(TAG, "User found. Document data: " + document.getData());

                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // No user found
                            Log.d(TAG, "No user found with device ID: " + deviceID);
                            Intent intent = new Intent(this, SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Error in query: ", task.getException());
                        Toast.makeText(this, "Error checking user status: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}