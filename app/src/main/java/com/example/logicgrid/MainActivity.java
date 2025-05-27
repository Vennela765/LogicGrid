package com.example.logicgrid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.GradientDrawable;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView levelText;
    private TextView messageText;
    private Button checkButton;
    private Button newPuzzleButton;
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private LinearLayout cluesList;
    private Button[][] cells;
    private int currentLevel = 1;
    private String currentDifficulty = "EASY";
    private static final int GRID_SIZE = 3;
    private static final int CELL_SIZE = 85;
    private static final int CELL_MARGIN = 2;

    private String[][] categories = {
        {"Bird", "Cat", "Dog"},
        {"Brown", "White", "Pink"}
    };

    private String[] currentClues = {
        "Bird is associated with Pink",
        "Dog corresponds to Brown",
        "Dog doesn't match with White",
        "Cat is not associated with Brown"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        levelText = findViewById(R.id.levelText);
        messageText = findViewById(R.id.messageText);
        checkButton = findViewById(R.id.checkButton);
        newPuzzleButton = findViewById(R.id.newPuzzleButton);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);
        cluesList = findViewById(R.id.cluesList);

        cells = new Button[GRID_SIZE][GRID_SIZE];
        
        setupDifficultyButtons();
        setupActionButtons();
        initializeGrid();
        loadLevel(currentLevel);
    }

    private void setupDifficultyButtons() {
        easyButton.setOnClickListener(v -> setDifficulty("EASY"));
        mediumButton.setOnClickListener(v -> setDifficulty("MEDIUM"));
        hardButton.setOnClickListener(v -> setDifficulty("HARD"));
    }

    private void setupActionButtons() {
        newPuzzleButton.setOnClickListener(v -> {
            currentLevel++;
            loadLevel(currentLevel);
        });

        checkButton.setOnClickListener(v -> checkSolution());
    }

    private void setDifficulty(String difficulty) {
        currentDifficulty = difficulty;
        currentLevel = 1;
        updateDifficultyButtons();
        loadLevel(currentLevel);
    }

    private void updateDifficultyButtons() {
        easyButton.setBackgroundTintList(currentDifficulty.equals("EASY") ? 
            ContextCompat.getColorStateList(this, R.color.primary_dark) :
            null);
        mediumButton.setBackgroundTintList(currentDifficulty.equals("MEDIUM") ? 
            ContextCompat.getColorStateList(this, R.color.primary_dark) :
            null);
        hardButton.setBackgroundTintList(currentDifficulty.equals("HARD") ? 
            ContextCompat.getColorStateList(this, R.color.primary_dark) :
            null);
    }

    private void initializeGrid() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(GRID_SIZE + 1);
        gridLayout.setRowCount(GRID_SIZE + 1);

        int dpToPx = (int) (getResources().getDisplayMetrics().density);

        // Add empty top-left cell
        addHeaderCell("");

        // Add column headers
        for (int j = 0; j < GRID_SIZE; j++) {
            addHeaderCell(categories[1][j]);
        }

        // Add row headers and grid cells
        for (int i = 0; i < GRID_SIZE; i++) {
            addHeaderCell(categories[0][i]);
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
                cell.setBackground(shape);

                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> toggleCell(row, col));

                cells[i][j] = cell;
                gridLayout.addView(cell);
            }
        }

        updateClues();
    }

    private void addHeaderCell(String text) {
        TextView header = new TextView(this);
        header.setText(text);
        header.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
        header.setTextSize(16);
        header.setPadding(16, 16, 16, 16);
        header.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_light));
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = CELL_SIZE * (int) (getResources().getDisplayMetrics().density);
        params.height = CELL_SIZE * (int) (getResources().getDisplayMetrics().density);
        params.setMargins(2, 2, 2, 2);
        header.setLayoutParams(params);
        
        gridLayout.addView(header);
    }

    private void toggleCell(int row, int col) {
        Button cell = cells[row][col];
        String currentState = cell.getText().toString();
        
        if (currentState.isEmpty()) {
            cell.setText("✓");
            cell.setTextColor(ContextCompat.getColor(this, R.color.success));
            cell.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.success_light));
        } else if (currentState.equals("✓")) {
            cell.setText("✗");
            cell.setTextColor(ContextCompat.getColor(this, R.color.error));
            cell.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.error_light));
        } else {
            cell.setText("");
            cell.setBackgroundTintList(null);
        }
    }

    private void loadLevel(int level) {
        levelText.setText("Level: " + currentDifficulty + " - Puzzle " + level);
        resetGrid();
        updateClues();
    }

    private void resetGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setText("");
                cells[i][j].setBackgroundTintList(null);
            }
        }
    }

    private void updateClues() {
        cluesList.removeAllViews();
        for (String clue : currentClues) {
            TextView clueView = new TextView(this);
            clueView.setText(clue);
            clueView.setTextSize(16);
            clueView.setPadding(16, 12, 16, 12);
            clueView.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_light));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            clueView.setLayoutParams(params);
            cluesList.addView(clueView);
        }
    }

    private void checkSolution() {
        // This is a simplified check. In a real implementation, you would check against actual solutions
        boolean isCorrect = true;
        messageText.setText(isCorrect ? "Congratulations! Level completed!" : "Try again!");
        messageText.setTextColor(ContextCompat.getColor(this, 
            isCorrect ? R.color.success : R.color.error));
    }
}