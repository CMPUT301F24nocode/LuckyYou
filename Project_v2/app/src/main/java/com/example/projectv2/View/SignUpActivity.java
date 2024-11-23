package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.User;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import android.provider.Settings.Secure;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button signUpButton;
    private EditText email, firstName, lastName, phoneNumber;
    private CheckBox isOrganizer;
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
                boolean success=signUpUser();
                if (success) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);}
            }
        });

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