/**
 * Utility class for QR code operations. Provides a method to convert a Bitmap to a SHA-256 hash
 * and encode it as a Base64 string.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.util.Base64;

/**
 * Utility class for operations related to QR codes, such as hashing a Bitmap image.
 */
public class qrUtils {

    /**
     * Converts a Bitmap to a SHA-256 hash and encodes it as a Base64 string.
     *
     * @param bitmap the Bitmap to be hashed
     * @return the Base64-encoded SHA-256 hash string of the Bitmap, or null if hashing fails
     */
    public static String getBitmapHash(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bitmapBytes = outputStream.toByteArray();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bitmapBytes);

            return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
