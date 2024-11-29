package com.example.projectv2.View;

import static android.content.Intent.getIntent;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.qrUtils;
import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.OutputStream;
import java.util.Objects;


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
        String name = intent.getStringExtra("name");

        // Generate QR code based on event description and poster URL
        generateQrCode(eventID);

        Button saveQrCodeButton = findViewById(R.id.saveQrCodeButton);
        saveQrCodeButton.setOnClickListener(view -> {
            // Get the QR code bitmap from the ImageView
            Bitmap qrBitmap = ((BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();
            saveImageToGallery(qrBitmap, name);
        });
    }

    private void generateQrCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);

            // Generate the hashed string from the bitmap
            String qrHashData = qrUtils.getBitmapHash(bitmap);

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

    public void saveImageToGallery(Bitmap bitmap, String name) {
        try {
            OutputStream fos;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LuckyYou");

                fos = getContentResolver().openOutputStream(
                        Objects.requireNonNull(getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
                );
            } else {
                // Save to Pictures directory for older Android versions
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                String imageName = name + ".png";
                fos = new java.io.FileOutputStream(imagesDir + "/" + imageName);
            }

            // Compress and save the bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

