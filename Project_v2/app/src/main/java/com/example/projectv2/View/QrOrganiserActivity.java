package com.example.projectv2.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.qrUtils;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


public class QrOrganiserActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_organiser);

        // Set up the top bar with title and back button
        topBarUtils.topBarSetup(this, "QR Code", View.INVISIBLE);

        qrCodeImageView = findViewById(R.id.imageView2);

        // Get event data passed from the previous activity
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        String eventDescription = intent.getStringExtra("description");
        String eventPosterUrl = intent.getStringExtra("posterUrl");

        // Generate QR code based on event description and poster URL
        generateQrCode(  "Event ID: " + eventID + "\nEvent Description: " + eventDescription + "\nEvent PosterURL: " + eventPosterUrl);
    }
    private void generateQrCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);

            // Generate the hashed string from the bitmap
            String qrHashData = qrUtils.getBitmapHash(bitmap);

            // Log and save to Firestore
//            Log.d("QR Hash", qrHashData);
            saveQrHashToFirestore(qrHashData);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void saveQrHashToFirestore(String qrHashData) {
        // Retrieve the event ID to locate the correct document
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");

        if (eventID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventID)
                    .update("qrHashData", qrHashData)  // Add the hashed string under 'qrHashData'
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "QR hash saved successfully"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error saving QR hash", e));
        }
    }
}

