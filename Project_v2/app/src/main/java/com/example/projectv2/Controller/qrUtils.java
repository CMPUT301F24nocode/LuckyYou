package com.example.projectv2.Controller;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.util.Base64;

public class qrUtils {

    // Convert Bitmap to SHA-256 Hash String
    public static String getBitmapHash(Bitmap bitmap) {
        try {
            // Convert Bitmap to Byte Array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bitmapBytes = outputStream.toByteArray();

            // Create SHA-256 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bitmapBytes);

            // Convert hash bytes to Base64 string
            return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
