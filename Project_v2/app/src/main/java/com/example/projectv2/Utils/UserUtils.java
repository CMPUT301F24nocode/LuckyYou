package com.example.projectv2.Utils;

import android.annotation.SuppressLint;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Utility class for user operations. Provides a method to get the user ID.
 */
public class UserUtils extends AppCompatActivity {
    public String getUserID(){
        @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }
}
