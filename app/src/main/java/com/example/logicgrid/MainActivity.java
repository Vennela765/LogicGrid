package com.example.logicgrid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView levelText;
    private TextView messageText;
    private Button checkButton;
    private Button[][] cells;
    private int currentLevel = 1;
    private boolean[][] solution;
    private static final int GRID_SIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        levelText = findViewById(R.id.levelText);
        messageText = findViewById(R.id.messageText);
        checkButton = findViewById(R.id.checkButton);

        cells = new Button[GRID_SIZE][GRID_SIZE];
        initializeGrid();
        loadLevel(currentLevel);

        checkButton.setOnClickListener(v -> checkSolution());
    }

    private void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Button cell = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 100;
                params.height = 100;
                params.setMargins(4, 4, 4, 4);
                cell.setLayoutParams(params);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setColor(ContextCompat.getColor(this, android.R.color.white));
                shape.setStroke(2, ContextCompat.getColor(this, android.R.color.black));
                cell.setBackground(shape);

                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> toggleCell(row, col));

                cells[i][j] = cell;
                gridLayout.addView(cell);
            }
        }
    }

    private void toggleCell(int row, int col) {
        Button cell = cells[row][col];
        boolean isSelected = cell.getTag() != null && (boolean) cell.getTag();
        
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setStroke(2, ContextCompat.getColor(this, android.R.color.black));
        
        if (isSelected) {
            shape.setColor(ContextCompat.getColor(this, android.R.color.white));
            cell.setTag(false);
        } else {
            shape.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
            cell.setTag(true);
        }
        cell.setBackground(shape);
    }

    private void loadLevel(int level) {
        levelText.setText("Level " + level);
        messageText.setText("");
        
        // Reset grid
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setTag(false);
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setColor(ContextCompat.getColor(this, android.R.color.white));
                shape.setStroke(2, ContextCompat.getColor(this, android.R.color.black));
                cells[i][j].setBackground(shape);
            }
        }

        // Set solution for current level
        solution = getLevelSolution(level);
    }

    private boolean[][] getLevelSolution(int level) {
        boolean[][] levelSolution = new boolean[GRID_SIZE][GRID_SIZE];
        
        switch (level) {
            case 1:
                // Simple pattern for level 1
                levelSolution[0][0] = true;
                levelSolution[1][1] = true;
                levelSolution[2][2] = true;
                levelSolution[3][3] = true;
                break;
            case 2:
                // X pattern for level 2
                levelSolution[0][0] = true;
                levelSolution[0][3] = true;
                levelSolution[1][1] = true;
                levelSolution[1][2] = true;
                levelSolution[2][1] = true;
                levelSolution[2][2] = true;
                levelSolution[3][0] = true;
                levelSolution[3][3] = true;
                break;
            case 3:
                // Box pattern for level 3
                levelSolution[0][0] = true;
                levelSolution[0][3] = true;
                levelSolution[3][0] = true;
                levelSolution[3][3] = true;
                break;
            default:
                // Default to simple pattern if level > 3
                levelSolution[0][0] = true;
                levelSolution[1][1] = true;
                levelSolution[2][2] = true;
                levelSolution[3][3] = true;
                break;
        }
        
        return levelSolution;
    }

    private void checkSolution() {
        boolean correct = true;
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                boolean isSelected = cells[i][j].getTag() != null && (boolean) cells[i][j].getTag();
                if (isSelected != solution[i][j]) {
                    correct = false;
                    break;
                }
            }
        }

        if (correct) {
            messageText.setText("Congratulations! Level completed!");
            if (currentLevel < 3) {
                currentLevel++;
                checkButton.postDelayed(() -> loadLevel(currentLevel), 1500);
            } else {
                messageText.setText("Congratulations! You've completed all levels!");
                checkButton.setEnabled(false);
            }
        } else {
            messageText.setText("Try again!");
        }
    }
}