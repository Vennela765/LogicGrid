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
import android.view.animation.AnimationUtils;

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

        checkButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            checkSolution();
        });
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
                shape.setColor(ContextCompat.getColor(this, R.color.cell_background));
                shape.setStroke(2 * dpToPx, ContextCompat.getColor(this, R.color.cell_border));
                shape.setCornerRadius(4 * dpToPx);
                cell.setBackground(shape);

                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                    toggleCell(row, col);
                });

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
        shape.setCornerRadius(4 * getResources().getDisplayMetrics().density);
        shape.setStroke(2, ContextCompat.getColor(this, R.color.cell_border));
        
        if (isSelected) {
            shape.setColor(ContextCompat.getColor(this, R.color.cell_background));
            cell.setText("");
            cell.setTag(false);
        } else {
            shape.setColor(ContextCompat.getColor(this, R.color.cell_selected));
            cell.setText("✓");
            cell.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
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

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (cells[i][j].getTag() != null && (boolean) cells[i][j].getTag()) {
                    rowCounts[i]++;
                    colCounts[j]++;
                }
            }
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            if (rowCounts[i] > 1 || colCounts[i] > 1) {
                isValid = false;
                break;
            }
        }

        if (!isValid) {
            messageText.setText("Only one selection allowed per row and column");
            messageText.setTextColor(ContextCompat.getColor(this, R.color.error));
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
                shape.setColor(ContextCompat.getColor(this, R.color.cell_background));
                shape.setStroke(2, ContextCompat.getColor(this, R.color.cell_border));
                shape.setCornerRadius(4 * getResources().getDisplayMetrics().density);
                cells[i][j].setBackground(shape);
            }
        }

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
            messageText.setTextColor(ContextCompat.getColor(this, R.color.success));
            
            if (currentLevel < 3) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setTitle("Level Complete!")
                       .setMessage("Ready for the next level?")
                       .setPositiveButton("Next Level", (dialog, which) -> {
                           currentLevel++;
                           loadLevel(currentLevel);
                       })
                       .setCancelable(false)
                       .show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setTitle("Congratulations!")
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
            messageText.setTextColor(ContextCompat.getColor(this, R.color.error));
        }
    }
}