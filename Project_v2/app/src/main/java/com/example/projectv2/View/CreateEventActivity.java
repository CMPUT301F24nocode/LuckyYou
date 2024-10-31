package com.example.projectv2.View;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        topBarUtils.topBarSetup(this, "New Event", View.INVISIBLE);

        Button nextButton = findViewById(R.id.create_event_next_button);
        nextButton.setOnClickListener(v -> showPopup());
    }

    private void showPopup(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_event_options);
        dialog.show();
    }
}
