package com.example.logicgrid;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class LevelCompleteDialog extends Dialog {
    private TextView levelTextView;
    private TextView performanceText;
    private ImageView[] stars;
    private OnDialogButtonClickListener listener;

    public interface OnDialogButtonClickListener {
        void onNextLevelClick();
        void onBackToLevelsClick();
    }

    public LevelCompleteDialog(Context context) {
        super(context);
        setupDialog();
    }

    private void setupDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.level_complete_popup);
        
        // Make dialog background transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        // Set dialog width to match parent with margins
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        // Initialize views
        levelTextView = findViewById(R.id.levelTextView);
        performanceText = findViewById(R.id.performanceText);
        
        // Initialize stars array
        stars = new ImageView[3];
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);

        // Setup buttons
        MaterialButton nextLevelButton = findViewById(R.id.nextLevelButton);
        MaterialButton backToLevelsButton = findViewById(R.id.backToLevelsButton);

        nextLevelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNextLevelClick();
            }
            dismiss();
        });

        backToLevelsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackToLevelsClick();
            }
            dismiss();
        });

        // Prevent dialog from being dismissed when clicking outside
        setCanceledOnTouchOutside(false);
    }

    public void setLevel(int level) {
        levelTextView.setText("Level " + level + " Complete!");
    }

    public void setStars(int starCount) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(i < starCount 
                ? android.R.drawable.btn_star_big_on 
                : android.R.drawable.btn_star_big_off);
        }
    }

    public void setPerformanceText(String text) {
        performanceText.setText(text);
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        this.listener = listener;
    }
} 