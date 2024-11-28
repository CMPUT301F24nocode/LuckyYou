package com.example.projectv2.Utils;

import android.annotation.SuppressLint;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class UserUtils extends AppCompatActivity {
    public String getUserID(){
        @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }
}
