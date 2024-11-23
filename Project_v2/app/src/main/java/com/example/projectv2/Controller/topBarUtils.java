/**
 * Utility class for configuring the top bar in an activity. Provides a method to set up
 * the top bar with a custom banner text, back button functionality, and the visibility of a "more" button.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.R;

/**
 * Utility class for setting up the top bar in an activity.
 */
public class topBarUtils {

    /**
     * Sets up the top bar with a banner text, back button functionality, and more button visibility.
     * The back button is configured to handle the back press action for AppCompatActivity instances.
     *
     * @param activity            the activity in which the top bar is being set up
     * @param bannerText          the text to display in the banner
     * @param moreButtonVisibility visibility setting for the "more" button (e.g., View.VISIBLE, View.INVISIBLE)
     */
    public static void topBarSetup(Activity activity, String bannerText, int moreButtonVisibility) {
        TextView bannerView = activity.findViewById(R.id.top_bar_banner);
        bannerView.setText(bannerText);

        ImageButton moreButton = activity.findViewById(R.id.more_settings_button);
        moreButton.setVisibility(moreButtonVisibility);

        ImageButton backButton = activity.findViewById(R.id.back_button);

        if (activity instanceof AppCompatActivity) {
            backButton.setOnClickListener(view -> ((AppCompatActivity) activity).getOnBackPressedDispatcher().onBackPressed());
        }
    }
}
