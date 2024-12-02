package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectv2.MainActivity;
import com.example.projectv2.R;

import java.util.Objects;

/**
 * Activity class for displaying a splash screen with animations and handling delayed transitions.
 *
 * <p>The splash screen displays a loading animation and optional message, then transitions
 * to a target activity after a specified delay.</p>
 */
public class SplashScreenActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private LottieAnimationView lottieAnimationView;
    private TextView splashScreenText;

    /**
     * Called when the activity is created.
     * Initializes UI components, retrieves intent data, and sets up a delayed transition to the target activity.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_splash_screen);

        // Initialize UI components
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        splashScreenText = findViewById(R.id.splash_screen_text);

        // Retrieve intent data
        Intent intent = getIntent();
        Bundle extras = intent.getBundleExtra("EXTRA_DATA");
        assert extras != null;
        String userID = extras.getString("userID");
        String message = intent.getStringExtra("message");
        Integer delay = intent.getIntExtra("delay", 1800);
        String targetActivity = intent.getStringExtra("TARGET_ACTIVITY");

        // Set splash screen message if provided
        if (message != null) {
            splashScreenText.setText(message);
        }

        // Set animation based on target activity
        if (Objects.equals(targetActivity, "com.example.projectv2.MainActivity")) {
            lottieAnimationView.setAnimation(R.raw.lottie_loading_events);
        }

        // Initialize handler and runnable for delayed transition
        handler = new Handler();
        runnable = () -> {
            try {
                // Dynamically load the target activity class
                Class<?> targetClass = Class.forName(targetActivity);
                Intent intent1 = new Intent(SplashScreenActivity.this, targetClass);

                // Pass along extras if provided
                if (extras != null) {
                    intent1.putExtras(extras);
                }
                startActivity(intent1);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                finish();
            }
        };

        // Schedule the runnable with the specified delay
        handler.postDelayed(runnable, delay);
    }

    /**
     * Cleans up the handler and runnable when the activity is destroyed.
     * Prevents memory leaks by removing pending callbacks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
