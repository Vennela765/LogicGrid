package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
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
        new AlertDialog.Builder(this)
            .setTitle(R.string.how_to_play)
            .setMessage(R.string.how_to_play_instructions)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}