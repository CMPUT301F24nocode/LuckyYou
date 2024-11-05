package com.example.projectv2.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        isOrganizer = findViewById(R.id.is_organizer);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });


    }

    private void signUpUser() {
        @SuppressLint("HardwareIds") String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        User newUser = new User(email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), isOrganizer.isChecked(),Long.parseLong(phoneNumber.getText().toString()),deviceID);
        //code that we are using currently
//        db.collection("Users").add(newUser).addOnSuccessListener(documentReference -> {
//            Log.d("User", "DocumentSnapshot added with ID: " + documentReference.getId());
//            //add any other code, like updating the list of users or something
//        }).addOnFailureListener(e -> {
//            Log.d("User", "Error adding document", e);
//        });
        //code that we will have actually
        db.collection("Users").document(newUser.getDeviceID()).set(newUser).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot added with ID: " + newUser.getDeviceID());
        }).addOnFailureListener(e -> {
            Log.d("User", "Error adding document", e);
        });

    }
}
