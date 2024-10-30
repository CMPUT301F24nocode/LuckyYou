package com.example.projectv2.View;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        topBarUtils.topBarSetup(this, "Profile", View.VISIBLE);

        ImageButton moreButton = findViewById(R.id.more_settings_button);
        moreButton.setOnClickListener(v -> showPopup());
    }

    private void showPopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.profile_overlay);
        dialog.show();
    }
}
