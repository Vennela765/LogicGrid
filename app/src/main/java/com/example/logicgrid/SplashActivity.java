package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private boolean isStartingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            ImageView logoImage = findViewById(R.id.splashLogo);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    startHomeActivity();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            logoImage.startAnimation(fadeIn);
        } catch (Exception e) {
            Log.e(TAG, "Error in SplashActivity onCreate", e);
            Toast.makeText(this, "Error loading app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startHomeActivity() {
        if (!isStartingActivity) {
            isStartingActivity = true;
            new Handler().postDelayed(() -> {
                try {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting HomeActivity", e);
                    Toast.makeText(this, "Error starting app", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, SPLASH_DURATION);
        }
    }
} 