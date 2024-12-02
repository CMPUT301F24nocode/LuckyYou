package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.projectv2.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRUserActivity extends CaptureActivity {

    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the custom layout
        setContentView(R.layout.custom_scanner);

        // Initialize the DecoratedBarcodeView
        barcodeView = findViewById(R.id.zxing_barcode_scanner);

        Log.d("QRUserActivity", "Custom QR Scanner Activity Launched");

        // Start the continuous scanning process
        barcodeView.setStatusText("Center the QR Code into the Frame");
        // turns on the flash light, we can implement a button for it at the end if we had time
//        barcodeView.setTorchOn();
        barcodeView.decodeContinuous(result -> {
            String scannedQRCode = result.getText();
            Log.d("QRUserActivity", "Scanned QR Code: " + scannedQRCode);

            if (scannedQRCode != null && !scannedQRCode.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("qrResult", scannedQRCode);
                setResult(RESULT_OK, resultIntent);
                finish(); // Finish the activity after scanning
            } else {
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume(); // Resume scanning
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause(); // Pause scanning
    }
}
