package com.example.logicgrid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.GradientDrawable;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.util.DisplayMetrics;

public class GameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView levelText;
    private TextView messageText;
    private Button checkButton;
    private Button newPuzzleButton;
    private LinearLayout cluesList;
    private Button[][] cells;
    private int currentLevel;
    private String currentDifficulty;
    private static final int CELL_MARGIN = 2;
    private GameLogic gameLogic;
    private int currentGridSize;
    private static final int LEVELS_PER_DIFFICULTY = 100; // Match with LevelSelectActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get difficulty, level and seed from intent
        currentDifficulty = getIntent().getStringExtra("difficulty");
        currentLevel = getIntent().getIntExtra("level", 1);
        long seed = getIntent().getLongExtra("seed", currentLevel); // Get seed or use level as fallback

        initializeViews();
        setupActionButtons();
        setupBackButton();
        currentGridSize = GameLogic.getDifficultySize(currentDifficulty);
        initializeGame(seed);
    }

    private void initializeViews() {
        gridLayout = findViewById(R.id.gridLayout);
        levelText = findViewById(R.id.levelText);
        messageText = findViewById(R.id.messageText);
        checkButton = findViewById(R.id.checkButton);
        newPuzzleButton = findViewById(R.id.newPuzzleButton);
        cluesList = findViewById(R.id.cluesList);
    }

    private void setupActionButtons() {
        newPuzzleButton.setOnClickListener(v -> {
            currentLevel = (currentLevel % LEVELS_PER_DIFFICULTY) + 1;
            initializeGame(generateSeed(currentDifficulty, currentLevel));
        });

        checkButton.setOnClickListener(v -> checkSolution());
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private long generateSeed(String difficulty, int level) {
        // Create a deterministic seed based on difficulty and level
        // This ensures the same puzzle is generated for the same level every time
        String seedString = difficulty + "_" + level;
        long seed = 0;
        for (char c : seedString.toCharArray()) {
            seed = 31 * seed + c;
        }
        return seed;
    }

    private int calculateCellSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        // Get screen width and height
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        
        // Use 70% of the smaller screen dimension
        int targetSize = Math.min(screenWidth, screenHeight) * 70 / 100;
        
        // Remove padding and margins (in pixels)
        int padding = (int) (32 * getResources().getDisplayMetrics().density); // 16dp padding on each side
        targetSize -= padding;
        
        // Calculate cell size based on grid size (add 1 for headers)
        return targetSize / (currentGridSize + 1);
    }

    private void initializeGame(long seed) {
        GameLogic.PuzzleData puzzleData = GameLogic.generatePuzzle(currentDifficulty, seed);
        if (puzzleData == null) {
            Toast.makeText(this, "Error generating puzzle", Toast.LENGTH_SHORT).show();
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
        int cellSizePx = calculateCellSize();
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
            header.setIncludeFontPadding(false);
            header.setLineSpacing(0, 1.0f);
            header.setTextSize(14); // Increased text size
            
            // Strict text constraints
            header.setSingleLine(false);
            header.setLines(2);
            header.setMaxLines(2);
            header.setEllipsize(android.text.TextUtils.TruncateAt.END);
            
            // Minimal padding to prevent text touching edges
            int paddingDp = 4;
            int paddingPx = paddingDp * dpToPx;
            header.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            
            header.setBreakStrategy(android.text.Layout.BREAK_STRATEGY_BALANCED);
            header.setHyphenationFrequency(android.text.Layout.HYPHENATION_FREQUENCY_FULL);
            header.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            header.setText(text);
            
            shape.setColor(ContextCompat.getColor(this, R.color.header_background));
            header.setBackground(shape);
            header.setLayoutParams(params);
            header.setElevation(2 * dpToPx);
            
            gridLayout.addView(header);
            return (T) header;
        } else {
            Button cell = new Button(this);
            
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
            
            cell.setPadding(0, 0, 0, 0);
            cell.setIncludeFontPadding(false);
            cell.setGravity(android.view.Gravity.CENTER);
            cell.setTextSize(16); // Increased text size for marks
            
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
            // Automatically move to next level with its deterministic puzzle
            currentLevel++;
            if (currentLevel > LEVELS_PER_DIFFICULTY) {
                currentLevel = 1;
                Toast.makeText(this, getString(R.string.all_levels_completed), Toast.LENGTH_LONG).show();
            }
            initializeGame(generateSeed(currentDifficulty, currentLevel));
        }
    }
}