package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.projectv2.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Custom activity for QR code scanning using the ZXing library.
 *
 * <p>This activity provides a customized QR code scanner interface,
 * handles scanning events continuously, and returns the scanned QR code
 * result to the calling activity.</p>
 */
public class QRUserActivity extends CaptureActivity {

    private DecoratedBarcodeView barcodeView;

    /**
     * Called when the activity is created.
     * Initializes the custom QR scanner layout and starts continuous QR code scanning.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the custom layout for the QR scanner
        setContentView(R.layout.custom_scanner);

        // Initialize the DecoratedBarcodeView
        barcodeView = findViewById(R.id.zxing_barcode_scanner);

        Log.d("QRUserActivity", "Custom QR Scanner Activity Launched");

        // Start continuous scanning with a custom status text
        barcodeView.setStatusText("Center the QR Code into the Frame");

        // Decode QR codes continuously
        barcodeView.decodeContinuous(result -> {
            String scannedQRCode = result.getText();
            Log.d("QRUserActivity", "Scanned QR Code: " + scannedQRCode);

            if (scannedQRCode != null && !scannedQRCode.isEmpty()) {
                // Return the scanned QR code result to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("qrResult", scannedQRCode);
                setResult(RESULT_OK, resultIntent);
                finish(); // Finish the activity after a successful scan
            } else {
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when the activity is resumed.
     * Resumes QR code scanning.
     */
    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    /**
     * Called when the activity is paused.
     * Pauses QR code scanning to conserve resources.
     */
    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
