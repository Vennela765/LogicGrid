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
        // EASY LEVELS - Focus on direct relationships and simple deductions
        if (difficulty.equals("EASY")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Bird", "Cat", "Dog"},
                            {"Brown", "White", "Black"}
                        },
                        new String[] {
                            "The Bird is not Brown",
                            "The Cat is White",
                            "The Dog is darker than the other pets"
                        },
                        new int[][] {
                            {0, 0, 1},  // Bird - Black
                            {0, 1, 0},  // Cat - White
                            {1, 0, 0}   // Dog - Brown
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"Pizza", "Burger", "Salad"},
                            {"Monday", "Tuesday", "Wednesday"}
                        },
                        new String[] {
                            "Pizza was ordered after Burger",
                            "Salad was not ordered on Wednesday",
                            "The Tuesday order was not Pizza"
                        },
                        new int[][] {
                            {0, 0, 1},  // Pizza - Wednesday
                            {1, 0, 0},  // Burger - Monday
                            {0, 1, 0}   // Salad - Tuesday
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Red", "Blue", "Green"},
                            {"Car", "Bike", "Bus"}
                        },
                        new String[] {
                            "The Blue vehicle has two wheels",
                            "The Car is not Green",
                            "The Red vehicle is larger than the Blue one"
                        },
                        new int[][] {
                            {0, 0, 1},  // Red - Bus
                            {0, 1, 0},  // Blue - Bike
                            {1, 0, 0}   // Green - Car
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Alice", "Bob", "Carol"},
                            {"Apple", "Banana", "Cherry"}
                        },
                        new String[] {
                            "Alice's fruit is red",
                            "Bob's fruit is yellow",
                            "Carol's fruit starts with the same letter as her name"
                        },
                        new int[][] {
                            {1, 0, 0},  // Alice - Apple
                            {0, 1, 0},  // Bob - Banana
                            {0, 0, 1}   // Carol - Cherry
                        }
                    );
            }
        }
        
        // MEDIUM LEVELS - Introduce conditional logic and multiple-step deductions
        if (difficulty.equals("MEDIUM")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Teacher", "Doctor", "Engineer"},
                            {"Morning", "Afternoon", "Evening"}
                        },
                        new String[] {
                            "If the Doctor works in the Morning, the Engineer works in the Evening",
                            "The Teacher doesn't work in the Afternoon",
                            "The person working in the Morning isn't the Engineer",
                            "The Doctor prefers later shifts than the Teacher"
                        },
                        new int[][] {
                            {1, 0, 0},  // Teacher - Morning
                            {0, 0, 1},  // Doctor - Evening
                            {0, 1, 0}   // Engineer - Afternoon
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"Piano", "Guitar", "Drums"},
                            {"Jazz", "Rock", "Blues"}
                        },
                        new String[] {
                            "The Piano isn't used in Rock music",
                            "If Guitar plays Blues, then Drums must play Rock",
                            "The Jazz instrument has strings",
                            "The Rock instrument is the loudest",
                            "Blues isn't played on Piano"
                        },
                        new int[][] {
                            {1, 0, 0},  // Piano - Jazz
                            {0, 0, 1},  // Guitar - Blues
                            {0, 1, 0}   // Drums - Rock
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Gold", "Silver", "Bronze"},
                            {"Running", "Swimming", "Cycling"}
                        },
                        new String[] {
                            "The Silver medal wasn't won in Running",
                            "If Gold was won in Swimming, Bronze wasn't in Cycling",
                            "The Running medal is more valuable than the Cycling one",
                            "The Swimming medal isn't Bronze",
                            "If Silver wasn't won in Swimming, then Gold was won in Running"
                        },
                        new int[][] {
                            {1, 0, 0},  // Gold - Running
                            {0, 1, 0},  // Silver - Swimming
                            {0, 0, 1}   // Bronze - Cycling
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Coffee", "Tea", "Juice"},
                            {"Small", "Medium", "Large"}
                        },
                        new String[] {
                            "The largest drink contains caffeine",
                            "If Tea is Medium, then Juice must be Small",
                            "Coffee isn't served in the smallest size",
                            "The Medium drink isn't as sweet as the Small one",
                            "If Coffee is Large, then Tea can't be Small"
                        },
                        new int[][] {
                            {0, 0, 1},  // Coffee - Large
                            {0, 1, 0},  // Tea - Medium
                            {1, 0, 0}   // Juice - Small
                        }
                    );
            }
        }
        
        // HARD LEVELS - Complex logical relationships and multi-step deductions
        if (difficulty.equals("HARD")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Python", "Java", "Ruby"},
                            {"Web", "Mobile", "Data"}
                        },
                        new String[] {
                            "If Python is used for Web, then Java isn't used for Mobile",
                            "The Data project doesn't use Ruby",
                            "If Java is used for Data, then Python must be used for Web",
                            "The Mobile project uses an object-oriented language",
                            "If Ruby isn't used for Web, then Python isn't used for Data",
                            "The Web project uses a scripting language"
                        },
                        new int[][] {
                            {1, 0, 0},  // Python - Web
                            {0, 1, 0},  // Java - Mobile
                            {0, 0, 1}   // Ruby - Data
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"London", "Paris", "Rome"},
                            {"Spring", "Summer", "Winter"}
                        },
                        new String[] {
                            "The city visited in Winter is further north than the one visited in Summer",
                            "If Paris is visited in Spring, then Rome must be visited in Summer",
                            "London isn't visited in the warmest season",
                            "The Spring destination is in a different country than the Winter one",
                            "If Rome isn't visited in Spring, then Paris must be visited in Winter",
                            "The Summer destination is known for its ancient history"
                        },
                        new int[][] {
                            {0, 1, 0},  // London - Summer
                            {0, 0, 1},  // Paris - Winter
                            {1, 0, 0}   // Rome - Spring
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Chess", "Cards", "Dice"},
                            {"Expert", "Amateur", "Beginner"}
                        },
                        new String[] {
                            "The Expert player prefers games of pure strategy",
                            "If the Amateur plays Cards, then the Beginner doesn't play Dice",
                            "The Dice player has more experience than the Cards player",
                            "If Chess isn't played by the Expert, then Cards must be played by the Amateur",
                            "The Beginner doesn't play a game involving chance",
                            "If the Amateur plays Dice, then the Expert must play Chess",
                            "The Cards player isn't the most experienced"
                        },
                        new int[][] {
                            {1, 0, 0},  // Chess - Expert
                            {0, 0, 1},  // Cards - Beginner
                            {0, 1, 0}   // Dice - Amateur
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Mystery", "Romance", "Fantasy"},
                            {"Morning", "Afternoon", "Evening"}
                        },
                        new String[] {
                            "The Evening reading session isn't for light-hearted stories",
                            "If Romance is read in the Morning, then Fantasy isn't read in the Afternoon",
                            "The Mystery book is read when the sun is up",
                            "If Fantasy is read in the Evening, then Romance must be read in the Afternoon",
                            "The Morning reader prefers plots with magical elements",
                            "The book read in the Afternoon has more dialogue than action",
                            "If Mystery isn't read in the Afternoon, then Fantasy must be read in the Morning",
                            "The Evening reading requires the most concentration"
                        },
                        new int[][] {
                            {0, 0, 1},  // Mystery - Evening
                            {0, 1, 0},  // Romance - Afternoon
                            {1, 0, 0}   // Fantasy - Morning
                        }
                    );
            }
        }
        
        return null;
    }
}