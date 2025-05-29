package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton playButton = findViewById(R.id.playButton);
        MaterialButton instructionsButton = findViewById(R.id.instructionsButton);

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LevelSelectActivity.class);
            startActivity(intent);
        });

        instructionsButton.setOnClickListener(v -> showInstructions());
    }

    private void showInstructions() {
        String[] instructions = {
            "Logic Grid Puzzles are solved by using the given clues to fill in a grid with ✓ (yes) and ✗ (no) marks.",
            "Each clue gives you information about relationships between items.",
            "Click a cell to cycle through ✓, ✗, and empty.",
            "Use logic to determine which combinations are possible and which are not.",
            "Each item in a category can only match with one item from each other category.",
            "Complete the puzzle by filling all cells correctly!"
        };

        SpannableString spannableString = new SpannableString(String.join("\n\n", instructions));
        int bulletGapWidth = (int) (16 * getResources().getDisplayMetrics().density);
        
        int start = 0;
        for (String instruction : instructions) {
            spannableString.setSpan(
                new BulletSpan(bulletGapWidth),
                start,
                start + instruction.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            start += instruction.length() + 2; // +2 for "\n\n"
        }

        new AlertDialog.Builder(this)
            .setTitle(R.string.how_to_play)
            .setMessage(spannableString)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}