package com.example.projectv2.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QrOrganiserActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_organiser);

        qrCodeImageView = findViewById(R.id.imageView2);
        db = FirebaseFirestore.getInstance();

        // Get event data passed from the previous activity
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("name");
        String eventDescription = intent.getStringExtra("description");
        String eventPosterUrl = intent.getStringExtra("posterUrl");
        String eventId = intent.getStringExtra("eventId"); // Make sure to pass eventId from the previous activity

        // Generate QR code data
        String qrData = "Event Name: " + eventName + "\nDescription: " + eventDescription + "\nPosterURL: " + eventPosterUrl;
        generateQrCode(qrData);

        // Generate hash for QR data and store it in Firestore
        String hashData = generateHash(qrData);
            storeHashData(eventId, hashData);
    }

    private void generateQrCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void storeHashData(String eventId, String hashData) {
        if (eventId != null && hashData != null) {
            db.collection("events").document(eventId)
                    .update("HashDataQR", hashData)
                    .addOnSuccessListener(aVoid -> Log.d("QrOrganiserActivity", "HashDataQR successfully written!"))
                    .addOnFailureListener(e -> Log.w("QrOrganiserActivity", "Error writing HashDataQR", e));
        } else {
            Log.w("QrOrganiserActivity", "Event ID or hash data is null");
        }
    }
}