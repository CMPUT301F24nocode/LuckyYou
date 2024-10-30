package com.example.projectv2.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.R;

public class topBarUtils {

//    Usage Example:
//      topBarUtils.topBarSetup(this, "Notifications", View.INVISIBLE);

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
