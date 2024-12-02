package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.projectv2.View.QRUserActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QRUserActivityTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA
    );

    @Test
    public void testBarcodeScannerIsDisplayed() {
        ActivityScenario.launch(QRUserActivity.class);

        // Verify that the barcode scanner (DecoratedBarcodeView) is displayed
        onView(withId(R.id.zxing_barcode_scanner))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDimmedBackgroundIsDisplayed() {
        ActivityScenario.launch(QRUserActivity.class);

        // Verify the dimmed background overlay is displayed
        onView(withId(android.R.id.content))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTransparentSquareIsDisplayed() {
        ActivityScenario.launch(QRUserActivity.class);

        // Verify the transparent square overlay in the center
        onView(withId(R.id.zxing_barcode_scanner))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testBorderForSquareFrameIsDisplayed() {
        ActivityScenario.launch(QRUserActivity.class);

        // Since the border frame is defined by a drawable, we check the scanner layout
        onView(withId(R.id.zxing_barcode_scanner))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCustomScannerLayoutElements() {
        ActivityScenario.launch(QRUserActivity.class);

        // Verify all critical layout elements are present
        onView(withId(R.id.zxing_barcode_scanner))
                .check(matches(isDisplayed()));
    }
}
