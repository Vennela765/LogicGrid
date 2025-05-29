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

public class GameActivity extends AppCompatActivity {
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
    private int currentLevel;
    private String currentDifficulty;
    private static final int CELL_SIZE = 85;
    private static final int CELL_MARGIN = 2;
    private GameLogic gameLogic;
    private int currentGridSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get difficulty and level from intent
        currentDifficulty = getIntent().getStringExtra("difficulty");
        currentLevel = getIntent().getIntExtra("level", 1);

        initializeViews();
        setupDifficultyButtons();
        setupActionButtons();
        currentGridSize = GameLogic.getDifficultySize(currentDifficulty);
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
    }

    private void setupDifficultyButtons() {
        easyButton.setOnClickListener(v -> setDifficulty("EASY"));
        mediumButton.setOnClickListener(v -> setDifficulty("MEDIUM"));
        hardButton.setOnClickListener(v -> setDifficulty("HARD"));
        updateDifficultyButtons();
    }

    private void setupActionButtons() {
        newPuzzleButton.setOnClickListener(v -> {
            currentLevel = (currentLevel % 6) + 1;
            initializeGame();
        });

        checkButton.setOnClickListener(v -> checkSolution());
    }

    private void setDifficulty(String difficulty) {
        currentDifficulty = difficulty;
        currentLevel = 1;
        currentGridSize = GameLogic.getDifficultySize(difficulty);
        cells = new Button[currentGridSize][currentGridSize];
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

    private void initializeGame() {
        GameLogic.PuzzleData puzzleData = GameLogic.generatePuzzle(currentDifficulty, currentLevel);
        if (puzzleData == null) {
            Toast.makeText(this, "No puzzle available for this difficulty and level", Toast.LENGTH_SHORT).show();
            return;
        }

        gameLogic = new GameLogic(currentGridSize, puzzleData.categories, puzzleData.clues, puzzleData.solution);
        levelText.setText(getString(R.string.level_format, currentDifficulty, currentLevel));
        cells = new Button[currentGridSize][currentGridSize];
        initializeGrid();
        updateClues();
    }

    private void initializeGrid() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(currentGridSize + 1);
        gridLayout.setRowCount(currentGridSize + 1);
        gridLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grid_background));
        
        // Add padding to the entire grid
        int gridPaddingDp = 8;
        int gridPaddingPx = (int) (gridPaddingDp * getResources().getDisplayMetrics().density);
        gridLayout.setPadding(gridPaddingPx, gridPaddingPx, gridPaddingPx, gridPaddingPx);
        
        // Add elevation to the grid
        gridLayout.setElevation(8 * getResources().getDisplayMetrics().density);
    
        String[][] categories = gameLogic.getCategories();
        int dpToPx = (int) (getResources().getDisplayMetrics().density);
    
        // Add empty top-left cell
        createCell("", true);
    
        // Add column headers
        for (int j = 0; j < currentGridSize; j++) {
            createCell(categories[1][j], true);
        }
    
        // Add row headers and grid cells
        for (int i = 0; i < currentGridSize; i++) {
            // Add row header
            createCell(categories[0][i], true);
            
            // Add grid cells
            for (int j = 0; j < currentGridSize; j++) {
                Button cell = createCell("", false);
                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> toggleCell(row, col));
                cells[i][j] = cell;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T createCell(String text, boolean isHeader) {
        int dpToPx = (int) getResources().getDisplayMetrics().density;
        int cellSizePx = CELL_SIZE * dpToPx;
        int marginPx = CELL_MARGIN * dpToPx;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cellSizePx;
        params.height = cellSizePx;
        params.setMargins(marginPx, marginPx, marginPx, marginPx);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8 * dpToPx);
        shape.setStroke(dpToPx, ContextCompat.getColor(this, R.color.grid_border));

        if (isHeader) {
            TextView header = new TextView(this);
            
            // Force fixed size and prevent any size changes
            header.setWidth(cellSizePx);
            header.setHeight(cellSizePx);
            header.setMinWidth(cellSizePx);
            header.setMinHeight(cellSizePx);
            header.setMaxWidth(cellSizePx);
            header.setMaxHeight(cellSizePx);
            
            // Center the text both horizontally and vertically
            header.setGravity(android.view.Gravity.CENTER);
            
            // Set text properties for consistent appearance
            header.setTextColor(ContextCompat.getColor(this, R.color.header_text));
            header.setIncludeFontPadding(false); // Remove extra font padding
            header.setLineSpacing(0, 1.0f); // Remove extra line spacing
            header.setTextSize(11); // Even smaller text size
            
            // Strict text constraints
            header.setSingleLine(false); // Allow multiple lines
            header.setLines(2); // Force exactly 2 lines
            header.setMaxLines(2);
            header.setEllipsize(android.text.TextUtils.TruncateAt.END);
            
            // Minimal padding to prevent text touching edges
            int paddingDp = 2;
            int paddingPx = paddingDp * dpToPx;
            header.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            
            // Advanced text layout settings
            header.setBreakStrategy(android.text.Layout.BREAK_STRATEGY_BALANCED);
            header.setHyphenationFrequency(android.text.Layout.HYPHENATION_FREQUENCY_FULL);
            
            // Prevent any text scaling
            header.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            
            // Set the text after configuring the TextView
            header.setText(text);
            
            shape.setColor(ContextCompat.getColor(this, R.color.header_background));
            header.setBackground(shape);
            header.setLayoutParams(params);
            header.setElevation(2 * dpToPx);
            
            gridLayout.addView(header);
            return (T) header;
        } else {
            Button cell = new Button(this);
            
            // Force fixed size and prevent any size changes
            cell.setWidth(cellSizePx);
            cell.setHeight(cellSizePx);
            cell.setMinWidth(cellSizePx);
            cell.setMinHeight(cellSizePx);
            cell.setMaxWidth(cellSizePx);
            cell.setMaxHeight(cellSizePx);
            
            shape.setColor(ContextCompat.getColor(this, R.color.cell_empty));
            cell.setBackground(shape);
            cell.setLayoutParams(params);
            cell.setElevation(4 * dpToPx);
            
            // Remove all padding and size constraints
            cell.setPadding(0, 0, 0, 0);
            cell.setIncludeFontPadding(false);
            
            // Center any text that might be added
            cell.setGravity(android.view.Gravity.CENTER);
            
            // Set text size to match headers
            cell.setTextSize(11);
            
            gridLayout.addView(cell);
            return (T) cell;
        }
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
            if (currentLevel == 6) {
                if (currentDifficulty.equals("EASY")) {
                    setDifficulty("MEDIUM");
                } else if (currentDifficulty.equals("MEDIUM")) {
                    setDifficulty("HARD");
                } else {
                    // Completed all levels
                    Toast.makeText(this, "Congratulations! You've completed all levels!", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                currentLevel++;
                initializeGame();
            }
        }
    }
}