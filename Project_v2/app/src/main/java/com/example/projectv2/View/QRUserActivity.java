package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRUserActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("QRUserActivity", "QR Scanner Activity Launched");
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // Use default camera
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String scannedQRCode = scanResult.getContents();
            Log.d("QRUserActivity", "Scanned QR Code: " + scannedQRCode);

            if (scannedQRCode != null && !scannedQRCode.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("qrResult", scannedQRCode);
                setResult(RESULT_OK, resultIntent);
            } else {
                Log.e("QRUserActivity", "QR Code is empty");
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
        finish();
    }
}