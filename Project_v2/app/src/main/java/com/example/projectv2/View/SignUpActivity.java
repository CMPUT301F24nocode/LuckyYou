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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.User;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;
import java.util.Objects;

/**
 * Activity class for user sign-up functionality.
 *
 * <p>This class handles user input, validates the phone number, Accesses the devices unique device ID,
 * stores user data in Firestore, and retrieves an FCM token for notifications.</p>
 */
public class SignUpActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseFirestore db;
    private Button signUpButton;
    private EditText email, firstName, lastName, phoneNumber;

    /**
     * Default constructor for initializing the Firestore instance.
     */
    public SignUpActivity() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Called when the activity is created.
     * Initializes UI components and sets up the sign-up button click listener.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Initialize UI elements
        signUpButton = findViewById(R.id.signup_button);
        email = findViewById(R.id.signup_email);
        firstName = findViewById(R.id.signup_firstname);
        lastName = findViewById(R.id.signup_secondname);
        phoneNumber = findViewById(R.id.signup_phonenumber);

        // Set up click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceID = signUpUser();
                if (!Objects.equals(deviceID, "")) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("deviceID", deviceID);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Handles the sign-up process for a new user.
     * Validates the phone number, generates a unique device ID, stores user data in Firestore,
     * and retrieves an FCM token for notifications.
     *
     * @return The device ID of the newly registered user, or an empty string if validation fails.
     */
    private String signUpUser() {
        // Validate phone number
        int phoneNumberError = isValidPhoneNumber(phoneNumber.getText().toString());
        if (phoneNumberError != 0) {
            if (phoneNumberError == 1) {
                phoneNumber.setError("Phone number must be 10 digits long or empty");
            } else {
                phoneNumber.setError("Phone number must contain only digits");
            }
            return "";
        }

        // Generate device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Create new User object
        User newUser = new User(
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                phoneNumber.getText().toString(),
                deviceID
        );
        newUser.setName(firstName.getText().toString() + " " + lastName.getText().toString());

        // Store user data in Firestore
        db.collection("Users").document(newUser.getDeviceID()).set(newUser)
                .addOnSuccessListener(aVoid -> Log.d("User", "DocumentSnapshot added with ID: " + newUser.getDeviceID()))
                .addOnFailureListener(e -> Log.d("User", "Error adding document", e));

        // Retrieve FCM token and update Firestore
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("fcmTokenResult", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();

                    // Update Firestore with the token
                    db.collection("Users")
                            .document(deviceID)
                            .set(Collections.singletonMap("fcmToken", token), SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("fcmTokenResult", "fcmToken field successfully updated/created!"))
                            .addOnFailureListener(e -> Log.w("fcmTokenResult", "Error updating/creating fcmToken field", e));
                });

        return deviceID;
    }

    /**
     * Validates a phone number to ensure it is either empty or 10 digits long,
     * and contains only numeric characters.
     *
     * @param phoneNumber The phone number to validate.
     * @return An error code:
     *         <ul>
     *         <li>0: Valid phone number</li>
     *         <li>1: Invalid length</li>
     *         <li>2: Contains non-numeric characters</li>
     *         </ul>
     */
    private int isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is empty or 10 digits long
        if (phoneNumber.isBlank()) {
            return 0;
        } else if (phoneNumber.length() != 10) {
            return 1;
        }

        // Check if the phone number contains only digits
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return 2;
            }
        }

        // Phone number is valid
        return 0;
    }
}
