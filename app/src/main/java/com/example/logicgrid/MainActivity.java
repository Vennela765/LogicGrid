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
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView levelText;
    private TextView messageText;
    private Button checkButton;
    private Button[][] cells;
    private int currentLevel = 1;
    private boolean[][] solution;
    private static final int GRID_SIZE = 4;
    private static final int CELL_SIZE = 85; // dp
    private static final int CELL_MARGIN = 2; // dp

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
        gridLayout.setColumnCount(GRID_SIZE);
        gridLayout.setRowCount(GRID_SIZE);

        int dpToPx = (int) (getResources().getDisplayMetrics().density);
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Button cell = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = CELL_SIZE * dpToPx;
                params.height = CELL_SIZE * dpToPx;
                params.setMargins(CELL_MARGIN * dpToPx, CELL_MARGIN * dpToPx, 
                                CELL_MARGIN * dpToPx, CELL_MARGIN * dpToPx);
                cell.setLayoutParams(params);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setColor(ContextCompat.getColor(this, android.R.color.white));
                shape.setStroke(2 * dpToPx, ContextCompat.getColor(this, android.R.color.black));
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
            cell.setText("");
            cell.setTag(false);
        } else {
            shape.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
            cell.setText("✓");
            cell.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            cell.setTextSize(24);
            cell.setTag(true);
        }
        cell.setBackground(shape);
        validateGrid();
    }

    private void validateGrid() {
        boolean isValid = true;
        int[] rowCounts = new int[GRID_SIZE];
        int[] colCounts = new int[GRID_SIZE];

        // Count selected cells in each row and column
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (cells[i][j].getTag() != null && (boolean) cells[i][j].getTag()) {
                    rowCounts[i]++;
                    colCounts[j]++;
                }
            }
        }

        // Check if any row or column has more than one selection
        for (int i = 0; i < GRID_SIZE; i++) {
            if (rowCounts[i] > 1 || colCounts[i] > 1) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            messageText.setText("Only one selection allowed per row and column");
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        } else {
            messageText.setText("");
        }
    }

    private void loadLevel(int level) {
        levelText.setText("Level " + level);
        messageText.setText("");
        
        // Reset grid
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setTag(false);
                cells[i][j].setText("");
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
                // Diagonal pattern
                levelSolution[0][0] = true;
                levelSolution[1][1] = true;
                levelSolution[2][2] = true;
                levelSolution[3][3] = true;
                break;
            case 2:
                // X pattern
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
                // Box pattern (corners)
                levelSolution[0][0] = true;
                levelSolution[0][3] = true;
                levelSolution[3][0] = true;
                levelSolution[3][3] = true;
                break;
            default:
                // Default to diagonal pattern
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
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            
            if (currentLevel < 3) {
                new AlertDialog.Builder(this)
                    .setTitle("Level Complete!")
                    .setMessage("Ready for the next level?")
                    .setPositiveButton("Next Level", (dialog, which) -> {
                        currentLevel++;
                        loadLevel(currentLevel);
                    })
                    .setCancelable(false)
                    .show();
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Congratulations!")
                    .setMessage("You've completed all levels!")
                    .setPositiveButton("Play Again", (dialog, which) -> {
                        currentLevel = 1;
                        loadLevel(currentLevel);
                    })
                    .setNegativeButton("Close", null)
                    .show();
            }
        } else {
            messageText.setText("Try again!");
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }
    }
}