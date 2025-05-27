package com.example.logicgrid;

public class GameLogic {
    public static final int EMPTY = 0;
    public static final int YES = 1;
    public static final int NO = 2;
    
    private final int size;
    private final int[][] grid;
    private final String[][] categories;
    private final String[] clues;
    private final int[][] solution;

    public GameLogic(int size, String[][] categories, String[] clues, int[][] solution) {
        this.size = size;
        this.grid = new int[size][size];
        this.categories = categories;
        this.clues = clues;
        this.solution = solution;
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

        // Rule 3: If a cell is marked YES, corresponding cells must be NO
        if (grid[row][col] == YES) {
            for (int i = 0; i < size; i++) {
                if (i != row && grid[i][col] == YES) return false;
                if (i != col && grid[row][i] == YES) return false;
            }
        }

        return true;
    }

    public boolean checkSolution() {
        // Check if the current grid matches the solution
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
        // Check if all cells are filled
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

    public static PuzzleData generatePuzzle(String difficulty, int level) {
        // Example puzzle data for EASY level 1
        if (difficulty.equals("EASY") && level == 1) {
            String[][] categories = {
                {"Bird", "Cat", "Dog"},
                {"Brown", "White", "Pink"}
            };
            
            String[] clues = {
                "Bird is associated with Pink",
                "Dog corresponds to Brown",
                "Dog doesn't match with White",
                "Cat is not associated with Brown"
            };
            
            int[][] solution = {
                {0, 0, 1},  // Bird - Pink
                {0, 1, 0},  // Cat - White
                {1, 0, 0}   // Dog - Brown
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        // Add more puzzles for different difficulties and levels
        return null;
    }
} 