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
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView levelText;
    private TextView messageText;
    private Button checkButton;
    private Button newPuzzleButton;
    private MaterialButton easyButton;
    private MaterialButton mediumButton;
    private MaterialButton hardButton;
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

    private String[][] easyClues = {
        {"Bird is associated with Pink", "Dog corresponds to Brown", 
         "Dog doesn't match with White", "Cat is not associated with Brown"},
        {"Cat matches with Pink", "Bird corresponds to White", 
         "Dog is paired with Brown", "Cat is not matched with White"}
    };

    private String[][] mediumClues = {
        {"Bird matches with White", "Cat corresponds to Pink", 
         "Dog is paired with Brown", "Bird is not matched with Pink"},
        {"Dog is associated with Brown", "Cat matches with White", 
         "Bird corresponds to Pink", "Dog is not paired with White"}
    };

    private String[][] hardClues = {
        {"Cat matches with Pink", "Dog is not associated with Pink", 
         "Bird corresponds to White", "Dog matches with Brown"},
        {"Bird is paired with Pink", "Cat corresponds to White", 
         "Dog matches with Brown", "Bird is not associated with Brown"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDifficultyButtons();
        setupActionButtons();
        initializeGrid();
        loadLevel(currentLevel);
    }

    private void initializeViews() {
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
    }

    private void setupDifficultyButtons() {
        easyButton.setOnClickListener(v -> setDifficulty("EASY"));
        mediumButton.setOnClickListener(v -> setDifficulty("MEDIUM"));
        hardButton.setOnClickListener(v -> setDifficulty("HARD"));
        updateDifficultyButtons();
    }

    private void setupActionButtons() {
        newPuzzleButton.setOnClickListener(v -> {
            currentLevel = (currentLevel % 2) + 1;
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
            ContextCompat.getColorStateList(this, R.color.primary) : null);
        easyButton.setStrokeWidth(currentDifficulty.equals("EASY") ? 0 : 2);
        easyButton.setTextColor(currentDifficulty.equals("EASY") ? 
            ContextCompat.getColor(this, R.color.white) : 
            ContextCompat.getColor(this, R.color.primary));

        mediumButton.setBackgroundTintList(currentDifficulty.equals("MEDIUM") ? 
            ContextCompat.getColorStateList(this, R.color.primary) : null);
        mediumButton.setStrokeWidth(currentDifficulty.equals("MEDIUM") ? 0 : 2);
        mediumButton.setTextColor(currentDifficulty.equals("MEDIUM") ? 
            ContextCompat.getColor(this, R.color.white) : 
            ContextCompat.getColor(this, R.color.primary));

        hardButton.setBackgroundTintList(currentDifficulty.equals("HARD") ? 
            ContextCompat.getColorStateList(this, R.color.primary) : null);
        hardButton.setStrokeWidth(currentDifficulty.equals("HARD") ? 0 : 2);
        hardButton.setTextColor(currentDifficulty.equals("HARD") ? 
            ContextCompat.getColor(this, R.color.white) : 
            ContextCompat.getColor(this, R.color.primary));
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
                shape.setColor(ContextCompat.getColor(this, R.color.white));
                shape.setStroke(2 * dpToPx, ContextCompat.getColor(this, R.color.grid_border));
                cell.setBackground(shape);

                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> toggleCell(row, col));

                cells[i][j] = cell;
                gridLayout.addView(cell);
            }
        }
    }

    private void addHeaderCell(String text) {
        TextView header = new TextView(this);
        header.setText(text);
        header.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        header.setTextSize(16);
        header.setPadding(16, 16, 16, 16);
        header.setBackgroundColor(ContextCompat.getColor(this, R.color.grid_header));
        
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
            cell.setTextColor(ContextCompat.getColor(this, R.color.button_green));
            cell.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.level_background));
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
        String[] currentClueSet = getCurrentClues();
        
        for (String clue : currentClueSet) {
            TextView clueView = new TextView(this);
            clueView.setText(clue);
            clueView.setTextSize(16);
            clueView.setPadding(16, 12, 16, 12);
            clueView.setTextColor(ContextCompat.getColor(this, R.color.clue_text));
            clueView.setBackgroundColor(ContextCompat.getColor(this, R.color.grid_header));
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            clueView.setLayoutParams(params);
            cluesList.addView(clueView);
        }
    }

    private String[] getCurrentClues() {
        String[][] clueSet;
        switch (currentDifficulty) {
            case "MEDIUM":
                clueSet = mediumClues;
                break;
            case "HARD":
                clueSet = hardClues;
                break;
            default:
                clueSet = easyClues;
                break;
        }
        return clueSet[currentLevel - 1];
    }

    private void checkSolution() {
        // This is a simplified check. In a real implementation, you would check against actual solutions
        boolean isCorrect = true;
        messageText.setText(isCorrect ? "Congratulations! Level completed!" : "Try again!");
        messageText.setTextColor(ContextCompat.getColor(this, 
            isCorrect ? R.color.button_green : R.color.error));
    }
}