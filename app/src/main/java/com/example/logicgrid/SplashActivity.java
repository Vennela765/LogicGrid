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
import com.example.logicgrid.data.DatabaseHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private boolean isStartingActivity = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Set content view first to ensure all resources are available
            setContentView(R.layout.activity_splash);

            // Initialize database helper and ensure database exists
            initializeDatabase();

            // Find and animate the logo
            ImageView logoImage = findViewById(R.id.splashLogo);
            if (logoImage != null) {
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                if (fadeIn != null) {
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
                } else {
                    // If animation failed, just start home activity after delay
                    new Handler().postDelayed(this::startHomeActivity, SPLASH_DURATION);
                }
            } else {
                // If logo image not found, show error
                throw new IllegalStateException("Required view splashLogo not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in SplashActivity onCreate", e);
            Toast.makeText(this, "Error loading app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeDatabase() {
        try {
            // Close any existing database connection
            if (dbHelper != null) {
                dbHelper.close();
            }

            // Create new database helper
            dbHelper = new DatabaseHelper(this);

            // Try to get a writable database to force creation/upgrade
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                // Close the database (it will be reopened when needed)
                db.close();
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error initializing database", e);
            // Delete the database file and try again
            deleteDatabase(DatabaseHelper.DATABASE_NAME);
            dbHelper = new DatabaseHelper(this);
            dbHelper.getWritableDatabase();
        }
    }

    private void startHomeActivity() {
        if (!isStartingActivity) {
            isStartingActivity = true;
            new Handler().postDelayed(() -> {
                try {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting HomeActivity", e);
                    Toast.makeText(this, "Error starting app: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }, SPLASH_DURATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            try {
                dbHelper.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing database", e);
            }
        }
    }
} 