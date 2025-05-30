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
import android.widget.Toast;

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
    private GameLogic gameLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDifficultyButtons();
        setupActionButtons();
        initializeGame();
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
            initializeGame();
        });

        checkButton.setOnClickListener(v -> checkSolution());
    }

    private void initializeGame() {
        GameLogic.PuzzleData puzzleData = GameLogic.generatePuzzle(currentDifficulty, currentLevel);
        if (puzzleData == null) {
            Toast.makeText(this, "No puzzle available for this difficulty and level", Toast.LENGTH_SHORT).show();
            return;
        }

        gameLogic = new GameLogic(GRID_SIZE, puzzleData.categories, puzzleData.clues, puzzleData.solution);
        levelText.setText("Level: " + currentDifficulty + " - Puzzle " + currentLevel);
        initializeGrid();
        updateClues();
    }

    private void setDifficulty(String difficulty) {
        currentDifficulty = difficulty;
        currentLevel = 1;
        updateDifficultyButtons();
        initializeGame();
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
        gridLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grid_background));
        
        // Add elevation to the grid
        gridLayout.setElevation(8 * getResources().getDisplayMetrics().density);
    
        String[][] categories = gameLogic.getCategories();
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
                shape.setColor(ContextCompat.getColor(this, R.color.cell_empty));
                shape.setStroke(2 * dpToPx, ContextCompat.getColor(this, R.color.grid_border));
                shape.setCornerRadius(8 * dpToPx);
                cell.setBackground(shape);
                cell.setElevation(4 * dpToPx);
    
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
        header.setTextColor(ContextCompat.getColor(this, R.color.header_text));
        header.setTextSize(16);
        header.setPadding(16, 16, 16, 16);
        header.setBackgroundColor(ContextCompat.getColor(this, R.color.header_background));
        header.setGravity(android.view.Gravity.CENTER);
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = CELL_SIZE * (int) (getResources().getDisplayMetrics().density);
        params.height = CELL_SIZE * (int) (getResources().getDisplayMetrics().density);
        params.setMargins(2, 2, 2, 2);
        header.setLayoutParams(params);
        
        // Add rounded corners to headers
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(ContextCompat.getColor(this, R.color.header_background));
        shape.setCornerRadius(8 * (int) (getResources().getDisplayMetrics().density));
        header.setBackground(shape);
        
        gridLayout.addView(header);
    }

    private void toggleCell(int row, int col) {
        Button cell = cells[row][col];
        boolean isValid = gameLogic.toggleCell(row, col);
        
        int state = gameLogic.getCellState(row, col);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setStroke(2 * (int) (getResources().getDisplayMetrics().density), 
                       ContextCompat.getColor(this, R.color.grid_border));
        shape.setCornerRadius(8 * (int) (getResources().getDisplayMetrics().density));
        
        switch (state) {
            case GameLogic.YES:
                cell.setText("✓");
                cell.setTextColor(ContextCompat.getColor(this, R.color.button_green));
                shape.setColor(ContextCompat.getColor(this, R.color.cell_yes));
                break;
            case GameLogic.NO:
                cell.setText("✗");
                cell.setTextColor(ContextCompat.getColor(this, R.color.error));
                shape.setColor(ContextCompat.getColor(this, R.color.cell_no));
                break;
            default:
                cell.setText("");
                shape.setColor(ContextCompat.getColor(this, R.color.cell_empty));
                break;
        }
        
        cell.setBackground(shape);
    
        if (!isValid) {
            cell.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateClues() {
        cluesList.removeAllViews();
        String[] clues = gameLogic.getClues();
        
        for (int i = 0; i < clues.length; i++) {
            TextView clueView = new TextView(this);
            String clueNumber = (i + 1) + ". ";
            clueView.setText(clueNumber + clues[i]);
            clueView.setTextSize(16);
            clueView.setPadding(24, 16, 24, 16);
            clueView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            clueView.setLineSpacing(8, 1);
            
            // Create rounded background for clue items
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(ContextCompat.getColor(this, R.color.clue_item_background));
            shape.setCornerRadius(12 * getResources().getDisplayMetrics().density);
            shape.setStroke(1, ContextCompat.getColor(this, R.color.primary_light));
            clueView.setBackground(shape);
            
            // Add elevation for a card-like effect
            clueView.setElevation(4 * getResources().getDisplayMetrics().density);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 8, 16, 8);
            clueView.setLayoutParams(params);
            
            // Add animation when clues appear
            clueView.setAlpha(0f);
            clueView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(i * 100)
                .start();
            
            cluesList.addView(clueView);
        }
    }

    private void checkSolution() {
        if (!gameLogic.isComplete()) {
            Toast.makeText(this, "Complete the puzzle first!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = gameLogic.checkSolution();
        messageText.setText(isCorrect ? "Congratulations! Level completed!" : "Try again!");
        messageText.setTextColor(ContextCompat.getColor(this, 
            isCorrect ? R.color.button_green : R.color.error));

        if (isCorrect) {
            // Automatically move to next level or difficulty
            if (currentLevel == 2) {
                if (currentDifficulty.equals("EASY")) {
                    setDifficulty("MEDIUM");
                } else if (currentDifficulty.equals("MEDIUM")) {
                    setDifficulty("HARD");
                }
            } else {
                currentLevel++;
                initializeGame();
            }
        }
    }
}