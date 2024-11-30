/**
 * Activity that manages user login. Verifies if a user exists in Firebase Firestore by checking the device ID.
 * If the user exists, they are redirected to the main activity. If not, they are prompted to sign up.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
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

/**
 * LoginActivity handles user login by checking if a user exists in Firebase Firestore based on the device ID.
 * If the user exists, it navigates to the main activity. Otherwise, it provides an option to sign up.
 */
public class LoginActivity extends AppCompatActivity {

    private TextView signUpButton;
    private FirebaseFirestore db;
    private static final String TAG = "LoginActivity";

    /**
     * Called when the activity is created. Initializes Firebase, retrieves the device ID,
     * and checks if a user exists in the Firebase Firestore based on the device ID.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
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

        // Set up sign-up button to navigate to SignUpActivity
        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Checks if a user document with the given device ID exists in the "Users" collection in Firestore.
     * If found, navigates to the main activity. If not, attempts a query fallback to search for the user.
     *
     * @param deviceID the unique identifier of the device, used as the document ID in Firestore
     */
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
                            Log.d(TAG, "Document data: " + document.getData());

                            // Navigate to MainActivity
                            Intent intent;
                            intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                            intent.putExtra("message", "Finding Best Events for You!");
                            intent.putExtra("delay",2500);
                            intent.putExtra("TARGET_ACTIVITY", MainActivity.class.getName());
                            Bundle extras = new Bundle();
                            extras.putString("userID", deviceID);
                            intent.putExtra("EXTRA_DATA", extras);
                            startActivity(intent);

                        } else {
                            // Document doesn't exist - try query as fallback
                            Log.d(TAG, "Document doesn't exist, trying query fallback");
                            queryUserCollection(deviceID);
                        }
                    } else {
                        Log.e(TAG, "Error getting document: ", task.getException());
                        Toast.makeText(this, "Error checking user status: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Queries the "Users" collection in Firestore for a document with the given device ID.
     * If found, navigates to the main activity. If not, navigates to the SignUpActivity.
     *
     * @param deviceID the unique identifier of the device, used as the document ID in Firestore
     */
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
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            Log.d(TAG, "User found. Document data: " + document.getData());

                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
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
