package com.example.logicgrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameLogic {
    public static final int EMPTY = 0;
    public static final int YES = 1;
    public static final int NO = 2;
    
    private final int size;
    private final int[][] grid;
    private final String[][] categories;
    private final String[] clues;
    private final int[][] solution;
    private static final Random random = new Random();

    private static final String[][] CATEGORY_SETS = {
        // People
        {"Doctor", "Teacher", "Engineer", "Artist", "Chef"},
        {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"},
        {"Red", "Blue", "Green", "Yellow", "Purple"},
        
        // Places
        {"Park", "Beach", "Museum", "Library", "Cafe"},
        {"Morning", "Noon", "Afternoon", "Evening", "Night"},
        {"Book", "Phone", "Laptop", "Tablet", "Camera"},
        
        // Activities
        {"Swimming", "Running", "Cycling", "Hiking", "Yoga"},
        {"Spring", "Summer", "Fall", "Winter", "Rainy"},
        {"Coffee", "Tea", "Juice", "Water", "Smoothie"},
        
        // Items
        {"Desk", "Chair", "Lamp", "Clock", "Plant"},
        {"Wood", "Metal", "Glass", "Plastic", "Fabric"},
        {"Small", "Medium", "Large", "Compact", "Oversized"}
    };

    private static final String[] CLUE_TEMPLATES = {
        "%s is not paired with %s",
        "If %s is with %s, then %s must be with %s",
        "%s is directly related to %s",
        "%s comes before %s in sequence",
        "%s and %s are mutually exclusive",
        "Either %s or %s must be true",
        "When %s occurs, %s follows",
        "%s implies %s is false",
        "%s and %s are complementary",
        "If not %s, then definitely %s"
    };

    public GameLogic(int size, String[][] categories, String[] clues, int[][] solution) {
        this.size = size;
        this.grid = new int[size][size];
        this.categories = categories;
        this.clues = clues;
        this.solution = solution;
    }

    public static PuzzleData generatePuzzle(String difficulty, long seed) {
        random.setSeed(seed);
        int size = getDifficultySize(difficulty);
        
        // Select random category sets
        List<String[]> allCategories = new ArrayList<>(Arrays.asList(CATEGORY_SETS));
        Collections.shuffle(allCategories, random);
        
        // Take first two category sets and trim to size
        String[][] categories = new String[2][size];
        for (int i = 0; i < 2; i++) {
            System.arraycopy(allCategories.get(i), 0, categories[i], 0, size);
        }

        // Generate random solution
        int[][] solution = new int[size][size];
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i++) indices.add(i);
        Collections.shuffle(indices, random);
        
        for (int i = 0; i < size; i++) {
            solution[i][indices.get(i)] = YES;
        }

        // Generate clues based on solution
        int numClues = getDifficultyClueCount(difficulty);
        List<String> generatedClues = new ArrayList<>();
        
        while (generatedClues.size() < numClues) {
            String template = CLUE_TEMPLATES[random.nextInt(CLUE_TEMPLATES.length)];
            int row1 = random.nextInt(size);
            int row2 = random.nextInt(size);
            int col1 = findSolutionColumn(solution, row1);
            int col2 = findSolutionColumn(solution, row2);
            
            String clue = String.format(template, 
                categories[0][row1], 
                categories[1][col1],
                categories[0][row2],
                categories[1][col2]);
            
            if (!generatedClues.contains(clue)) {
                generatedClues.add(clue);
            }
        }

        return new PuzzleData(categories, generatedClues.toArray(new String[0]), solution);
    }

    private static int findSolutionColumn(int[][] solution, int row) {
        for (int j = 0; j < solution[row].length; j++) {
            if (solution[row][j] == YES) return j;
        }
        return 0;
    }

    private static int getDifficultyClueCount(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return 3;
            case "MEDIUM":
                return 5;
            case "HARD":
                return 7;
            default:
                return 3;
        }
    }

    public static int getDifficultySize(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return 3;
            case "MEDIUM":
                return 4;
            case "HARD":
                return 5;
            default:
                return 3;
        }
    }

    public boolean toggleCell(int row, int col) {
        grid[row][col] = (grid[row][col] + 1) % 3;
        return validateMove(row, col);
    }

    private boolean validateMove(int row, int col) {
        // Rule 1: Each row must have exactly one YES
        int yesCount = 0;
        for (int j = 0; j < size; j++) {
            if (grid[row][j] == YES) yesCount++;
        }
        if (yesCount > 1) return false;

        // Rule 2: Each column must have exactly one YES
        yesCount = 0;
        for (int i = 0; i < size; i++) {
            if (grid[i][col] == YES) yesCount++;
        }
        if (yesCount > 1) return false;

        return true;
    }

    public boolean checkSolution() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (solution[i][j] == YES && grid[i][j] != YES) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isComplete() {
        int yesCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == YES) yesCount++;
            }
        }
        return yesCount == size;
    }

    public void clearGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }

    public String[] getClues() {
        return clues;
    }

    public String[][] getCategories() {
        return categories;
    }

    public int getCellState(int row, int col) {
        return grid[row][col];
    }

    public static class PuzzleData {
        public final String[][] categories;
        public final String[] clues;
        public final int[][] solution;

        public PuzzleData(String[][] categories, String[] clues, int[][] solution) {
            this.categories = categories;
            this.clues = clues;
            this.solution = solution;
        }
    }
}