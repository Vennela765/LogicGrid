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
        
        // EASY level 2 - slightly more challenging
        if (difficulty.equals("EASY") && level == 2) {
            String[][] categories = {
                {"Mary", "Alice", "John"},
                {"Red", "Blue", "Purple"}
            };
            
            String[] clues = {
                "The person who likes Purple is not John",
                "Mary doesn't like Blue",
                "The person who likes Red is not Alice",
                "If Alice doesn't like Purple, then Mary likes Red"
            };
            
            int[][] solution = {
                {1, 0, 0},  // Mary - Red
                {0, 0, 1},  // Alice - Purple
                {0, 1, 0}   // John - Blue
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        
        // MEDIUM level 1 - more complex logical deduction
        if (difficulty.equals("MEDIUM") && level == 1) {
            String[][] categories = {
                {"Teacher", "Doctor", "Engineer"},
                {"Tennis", "Chess", "Swimming"}
            };
            
            String[] clues = {
                "Either the Doctor or the Engineer plays Tennis",
                "If the Teacher plays Chess, then the Doctor doesn't play Swimming",
                "The person who plays Swimming is not the Engineer",
                "The Teacher doesn't play Tennis"
            };
            
            int[][] solution = {
                {0, 1, 0},  // Teacher - Chess
                {0, 0, 1},  // Doctor - Swimming
                {1, 0, 0}   // Engineer - Tennis
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        
        // MEDIUM level 2 - comparative clues
        if (difficulty.equals("MEDIUM") && level == 2) {
            String[][] categories = {
                {"Apple", "Banana", "Cherry"},
                {"Monday", "Wednesday", "Friday"}
            };
            
            String[] clues = {
                "The fruit bought on Monday was purchased before the Cherry",
                "The Banana was not bought on the same day as the Cherry",
                "If the Apple was bought on Wednesday, then the Cherry was bought on Friday",
                "The fruit bought on Wednesday is either the Apple or the Banana"
            };
            
            int[][] solution = {
                {0, 1, 0},  // Apple - Wednesday
                {1, 0, 0},  // Banana - Monday
                {0, 0, 1}   // Cherry - Friday
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        
        // HARD level 1 - complex logical relationships
        if (difficulty.equals("HARD") && level == 1) {
            String[][] categories = {
                {"Painter", "Writer", "Musician"},
                {"Paris", "London", "Rome"}
            };
            
            String[] clues = {
                "If the Musician lives in Paris, then the Writer doesn't live in London",
                "The Painter lives in either London or Rome",
                "If the Writer lives in Rome, then the Musician lives in London",
                "The person who lives in Paris is not the Writer",
                "If the Painter doesn't live in London, then the Musician lives in Paris"
            };
            
            int[][] solution = {
                {0, 0, 1},  // Painter - Rome
                {0, 1, 0},  // Writer - London
                {1, 0, 0}   // Musician - Paris
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        
        // HARD level 2 - very complex logical deduction
        if (difficulty.equals("HARD") && level == 2) {
            String[][] categories = {
                {"History", "Science", "Math"},
                {"Book", "Journal", "Online"}
            };
            
            String[] clues = {
                "The History resource is either the Book or the Journal",
                "If Science is studied from the Journal, then Math is not studied Online",
                "If History is studied from the Book, then Science is studied Online",
                "The subject studied from the Journal is not Math",
                "If Math is not studied from the Book, then History is not studied from the Journal",
                "The Science resource is not the same type as the Math resource"
            };
            
            int[][] solution = {
                {1, 0, 0},  // History - Book
                {0, 0, 1},  // Science - Online
                {0, 1, 0}   // Math - Journal
            };
            
            return new PuzzleData(categories, clues, solution);
        }
        
        return null;
    }
}