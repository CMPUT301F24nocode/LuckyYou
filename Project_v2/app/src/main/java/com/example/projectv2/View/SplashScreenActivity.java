package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectv2.MainActivity;
import com.example.projectv2.R;

public class SplashScreenActivity extends AppCompatActivity {
private Handler handler;
private Runnable runnable;
private LottieAnimationView lottieAnimationView;
private TextView splashScreenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        splashScreenText=findViewById(R.id.splash_screen_text);


        Intent intent=getIntent();
        Bundle extras=intent.getBundleExtra("EXTRA_DATA");
        String message=intent.getStringExtra("message");
        if(message!=null){
            splashScreenText.setText(message);
        }
        String targetActivity=intent.getStringExtra("TARGET_ACTIVITY");

        handler=new Handler();

        runnable= ()->{
            try{
                Class<?> targetClass=Class.forName(targetActivity);
                Intent intent1=new Intent(SplashScreenActivity.this,targetClass);
                if(extras!=null){
                    intent1.putExtras(extras);
                }
                startActivity(intent1);
                finish();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
                finish();
            }
        };
        handler.postDelayed(runnable,3000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
        handler.removeCallbacks(runnable);
    }}
}