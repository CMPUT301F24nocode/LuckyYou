package com.example.projectv2.View;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
                signUpUser();
            }
        });

    }

    private void signUpUser() {
        String deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        User newUser = new User(email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), Long.parseLong(phoneNumber.getText().toString()),deviceID);
        db.collection("Users").add(newUser).addOnSuccessListener(documentReference -> {
            Log.d("User", "DocumentSnapshot added with ID: " + documentReference.getId());
            //add any other code, like updating the list of users or something
        }).addOnFailureListener(e -> {
            Log.d("User", "Error adding document", e);
        });

    }
}
