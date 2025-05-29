package com.example.logicgrid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Animatable;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.util.DisplayMetrics;
import android.text.Layout;
import android.os.Build;

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
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        density = getResources().getDisplayMetrics().density;
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
            seed = 31L * seed + c;
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
            Toast.makeText(this, R.string.error_generating_puzzle, Toast.LENGTH_SHORT).show();
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
        int gridPaddingPx = (int) (gridPaddingDp * density);
        gridLayout.setPadding(gridPaddingPx, gridPaddingPx, gridPaddingPx, gridPaddingPx);
        
        // Add elevation to the grid
        gridLayout.setElevation(8 * density);
    
        String[][] categories = gameLogic.getCategories();
    
        // Add empty top-left cell with star
        createCell("", true, true);
    
        // Add column headers
        for (int j = 0; j < currentGridSize; j++) {
            createCell(categories[1][j], true, false);
        }
    
        // Add row headers and grid cells
        for (int i = 0; i < currentGridSize; i++) {
            // Add row header
            createCell(categories[0][i], true, false);
            
            // Add grid cells
            for (int j = 0; j < currentGridSize; j++) {
                Button cell = createCell("", false, false);
                final int row = i;
                final int col = j;
                cell.setOnClickListener(v -> toggleCell(row, col));
                cells[i][j] = cell;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T createCell(String text, boolean isHeader, boolean isTopLeft) {
        int cellSizePx = calculateCellSize();
        int marginPx = (int) (CELL_MARGIN * density);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cellSizePx;
        params.height = cellSizePx;
        params.setMargins(marginPx, marginPx, marginPx, marginPx);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(8 * density);
        shape.setStroke((int) density, ContextCompat.getColor(this, R.color.grid_border));

        if (isHeader) {
            if (isTopLeft) {
                // Create a special ImageButton for the top-left cell to better handle the animated star
                ImageButton starCell = new ImageButton(this);
                
                // Set exact same layout parameters as other cells
                GridLayout.LayoutParams starParams = new GridLayout.LayoutParams(params);
                starParams.width = cellSizePx;
                starParams.height = cellSizePx;
                starCell.setLayoutParams(starParams);
                
                // Set minimum dimensions
                starCell.setMinimumWidth(cellSizePx);
                starCell.setMinimumHeight(cellSizePx);
                
                starCell.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                
                // Create border shape with the same style as other cells
                GradientDrawable borderShape = new GradientDrawable();
                borderShape.setShape(GradientDrawable.RECTANGLE);
                borderShape.setColor(ContextCompat.getColor(this, R.color.cell_empty));
                borderShape.setStroke((int) density, ContextCompat.getColor(this, R.color.grid_border));
                borderShape.setCornerRadius(8 * density);
                starCell.setBackground(borderShape);
                
                // Use same padding as other cells
                int paddingPx = (int) (4 * density);
                starCell.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                
                try {
                    // Load the animated drawable
                    Drawable starDrawable = AppCompatResources.getDrawable(this, R.drawable.star_animation);
                    if (starDrawable != null) {
                        // Set the drawable and start animation
                        starCell.setImageDrawable(starDrawable);
                        // Scale the star to fit cell
                        starCell.setScaleX(1.0f);
                        starCell.setScaleY(1.0f);
                        if (starDrawable instanceof Animatable) {
                            ((Animatable) starDrawable).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                gridLayout.addView(starCell);
                return (T) starCell;
            } else {
                TextView header = new TextView(this);
                header.setWidth(cellSizePx);
                header.setHeight(cellSizePx);
                header.setMinWidth(cellSizePx);
                header.setMinHeight(cellSizePx);
                header.setMaxWidth(cellSizePx);
                header.setMaxHeight(cellSizePx);
                header.setGravity(android.view.Gravity.CENTER);
                header.setTextColor(ContextCompat.getColor(this, R.color.header_text));
                header.setIncludeFontPadding(false);
                header.setLineSpacing(0, 1.0f);
                header.setTextSize(14);
                header.setSingleLine(false);
                header.setLines(2);
                header.setMaxLines(2);
                header.setEllipsize(android.text.TextUtils.TruncateAt.END);
                
                int paddingDp = 4;
                int paddingPx = (int) (paddingDp * density);
                header.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    header.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
                    header.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL);
                }
                header.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
                header.setText(text);
                
                shape.setColor(ContextCompat.getColor(this, R.color.header_background));
                header.setBackground(shape);
                header.setLayoutParams(params);
                header.setElevation(2 * density);
                
                gridLayout.addView(header);
                return (T) header;
            }
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
            cell.setElevation(4 * density);
            cell.setPadding(0, 0, 0, 0);
            cell.setIncludeFontPadding(false);
            cell.setGravity(android.view.Gravity.CENTER);
            cell.setTextSize(16);
            
            // Add animated star drawable to empty cells using AppCompatResources
            try {
                android.graphics.drawable.Drawable starDrawable = androidx.appcompat.content.res.AppCompatResources
                    .getDrawable(this, R.drawable.star_animation);
                if (starDrawable != null) {
                    cell.setForeground(starDrawable);
                    if (starDrawable instanceof android.graphics.drawable.Animatable) {
                        ((android.graphics.drawable.Animatable) starDrawable).start();
                    }
                }
            } catch (Exception e) {
                // Fallback in case animation fails
                cell.setForeground(null);
            }
            
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
        shape.setStroke((int) (2 * density), ContextCompat.getColor(this, R.color.grid_border));
        shape.setCornerRadius(8 * density);
        
        switch (state) {
            case GameLogic.YES:
                cell.setText("✓");
                cell.setTextColor(ContextCompat.getColor(this, R.color.button_green));
                shape.setColor(ContextCompat.getColor(this, R.color.cell_yes));
                cell.setForeground(null);
                break;
            case GameLogic.NO:
                cell.setText("✗");
                cell.setTextColor(ContextCompat.getColor(this, R.color.error));
                shape.setColor(ContextCompat.getColor(this, R.color.cell_no));
                cell.setForeground(null);
                break;
            default:
                cell.setText("");
                shape.setColor(ContextCompat.getColor(this, R.color.cell_empty));
                // Add animated star drawable using AppCompatResources
                try {
                    android.graphics.drawable.Drawable starDrawable = androidx.appcompat.content.res.AppCompatResources
                        .getDrawable(this, R.drawable.star_animation);
                    if (starDrawable != null) {
                        cell.setForeground(starDrawable);
                        if (starDrawable instanceof android.graphics.drawable.Animatable) {
                            ((android.graphics.drawable.Animatable) starDrawable).start();
                        }
                    }
                } catch (Exception e) {
                    // Fallback in case animation fails
                    cell.setForeground(null);
                }
                break;
        }
        
        cell.setBackground(shape);
    
        if (!isValid) {
            cell.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            Toast.makeText(this, R.string.invalid_move, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateClues() {
        cluesList.removeAllViews();
        String[] clues = gameLogic.getClues();
        
        for (int i = 0; i < clues.length; i++) {
            TextView clueView = new TextView(this);
            clueView.setText(getString(R.string.clue_format, i + 1, clues[i]));
            clueView.setTextSize(16);
            clueView.setPadding(24, 16, 24, 16);
            clueView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            clueView.setLineSpacing(8, 1);
            
            // Create rounded background for clue items
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(ContextCompat.getColor(this, R.color.clue_item_background));
            shape.setCornerRadius(12 * density);
            shape.setStroke(1, ContextCompat.getColor(this, R.color.primary_light));
            clueView.setBackground(shape);
            
            // Add elevation for a card-like effect
            clueView.setElevation(4 * density);
            
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
            Toast.makeText(this, R.string.complete_puzzle, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = gameLogic.checkSolution();
        messageText.setText(getString(isCorrect ? R.string.congratulations : R.string.try_again));
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